package com.wzw.ziweishopcity.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wzw.ziweishopcity.order.entity.OrderItemEntity;
import com.wzw.ziweishopcity.order.enume.OrderStatusEnum;
import com.wzw.ziweishopcity.order.exception.SkuWareLockException;
import com.wzw.ziweishopcity.order.feign.CartFeignService;
import com.wzw.ziweishopcity.order.feign.MemberFeignService;
import com.wzw.ziweishopcity.order.feign.ProductFeignService;
import com.wzw.ziweishopcity.order.feign.WmsFeignServcie;
import com.wzw.ziweishopcity.order.interceptor.AuthInterceptor;
import com.wzw.ziweishopcity.order.service.OrderItemService;
import com.wzw.ziweishopcity.order.vo.*;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;
import com.wzw.ziweishopcity.order.dao.OrderDao;
import com.wzw.ziweishopcity.order.entity.OrderEntity;
import com.wzw.ziweishopcity.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    WmsFeignServcie wmsFeignServcie;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Transactional
    @Override
    public void saveSeckillOrderMessage(SeckillOrderVo seckillOrderVo) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setStatus(0);
        orderEntity.setOrderSn(seckillOrderVo.getOrderSn());
//        orderEntity.setOrderItemEntities();有空我远程调用写一下就行
        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(new Date());
        orderEntity.setPayAmount(seckillOrderVo.getSeckillPrice());
        orderEntity.setTotalAmount(seckillOrderVo.getSeckillPrice());
        orderEntity.setMemberUsername(seckillOrderVo.getUsername());
        this.save(orderEntity);
        //秒杀一次肯定只有一种商品，所以一个OrderItemEntity足够
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(seckillOrderVo.getOrderSn());
        orderItemEntity.setSkuId(seckillOrderVo.getSkuId());
        orderItemEntity.setRealAmount(seckillOrderVo.getSeckillPrice());
        orderItemEntity.setSkuQuantity(seckillOrderVo.getNum());
        orderItemService.save(orderItemEntity);
    }

    @Transactional
    @Override
    public ResultOrderSubmitVo toSubmitOrder(OrderSubmitVo orderSubmitVo) {
        String redisLua = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
        String username = (String) AuthInterceptor.th.get();
        ResultOrderSubmitVo resultOrderSubmitVo = new ResultOrderSubmitVo();//订单创建的结果
        Long result =
                redisTemplate.execute(new DefaultRedisScript<Long>(redisLua, Long.class),
                        Arrays.asList("order:token:" + username), orderSubmitVo.getToken());//lua脚本保证提交订单操作的原子性、幂等性
        if (result != null && result == 1)//如果lua脚本成功执行
        {
            //如果lua脚本成功执行，我们可以创建订单信息
            OrderCreateVo orderCreateVo = new OrderCreateVo();//订单、订单项集合、订单总金额
            OrderEntity orderEntity = new OrderEntity();
            orderCreateVo.setOrderEntity(orderEntity);
            orderEntity.setOrderSn(IdWorker.getTimeId());//设置订单号，使用MybatisPlus提供的工具IdWorker
            orderEntity.setMemberUsername(username);//设置订单用户名
            //设置用户选中的地址
            List<MemberReceiveAddress> addresses = memberFeignService.addresses(username);
            addresses = addresses.stream().filter(e -> {
                return e.getId() == orderSubmitVo.getAddrId();//订单金额、幂等性令牌、地址的id、订单的备注
            }).collect(Collectors.toList());
            if (addresses != null && addresses.size() > 0) {
                MemberReceiveAddress memberReceiveAddress = addresses.get(0);
                orderEntity.setReceiverPhone(memberReceiveAddress.getPhone());//电话
                orderEntity.setReceiverCity(memberReceiveAddress.getCity());//城市
                orderEntity.setReceiverDetailAddress(memberReceiveAddress.getDetailAddress());//详细地址
                orderEntity.setReceiverRegion(memberReceiveAddress.getRegion());//小区
                orderEntity.setReceiverProvince(memberReceiveAddress.getProvince());//省份
                orderEntity.setReceiverPostCode(memberReceiveAddress.getPostCode());//邮编
            }
            //获取该用户购物车中选中状态的购物项，放进订单详情项中
            List<OrderItemVo> item = cartFeignService.item();//订单购物项集合
            BigDecimal totalPrice = new BigDecimal("0");
            for (OrderItemVo orderItemVo : item) {//计算订单总金额
                totalPrice = totalPrice.add(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())));
            }
            orderEntity.setIntegration(totalPrice.intValue());//整个订单可获得的总积分
            orderEntity.setAutoConfirmDay(7);//设置订单N天自动确认收货
            orderEntity.setTotalAmount(totalPrice);//整个订单总金额
            orderEntity.setPromotionAmount(new BigDecimal("0"));//优惠额度
            orderEntity.setPayAmount(totalPrice);//整个订单应付金额
            orderEntity.setFreightAmount(new BigDecimal("0"));//订单的运费
            orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());//设置订单状态为待付款，这很重要哦
            resultOrderSubmitVo.setOrderEntity(orderEntity);//订单、创建订单的结果
            List<OrderItemEntity> orderItemEntitys = item.stream().map(e -> {
                OrderItemEntity orderItemEntity = new OrderItemEntity();
                //0、订单号信息
                orderItemEntity.setOrderSn(orderEntity.getOrderSn());
                //1、sku信息
                String attrsVals = StringUtils.collectionToDelimitedString(e.getSkuAttr(), ";");//将集合变为字符串
                orderItemEntity.setSkuAttrsVals(attrsVals);
                orderItemEntity.setSkuQuantity(e.getCount());
                orderItemEntity.setSkuId(e.getSkuId());
                orderItemEntity.setSkuPic(e.getImage());
                orderItemEntity.setCouponAmount(new BigDecimal("0"));//优惠卷金额
                orderItemEntity.setIntegrationAmount(new BigDecimal("0"));//积分金额
                orderItemEntity.setGiftGrowth(e.getPrice().intValue());//该订单项的成长值
                orderItemEntity.setGiftIntegration(e.getPrice().intValue());//该订单项的积分
                orderItemEntity.setRealAmount(e.getPrice().multiply(new BigDecimal(e.getCount())));//该订单项的真实价格
                orderItemEntity.setPromotionAmount(new BigDecimal("0"));//该订单项的优惠金额
                orderItemEntity.setSkuPrice(e.getPrice());//该订单项商品的单价
                //2、获取spu信息
                SpuInfoVo spuInfoVo = productFeignService.spuBySku(e.getSkuId());
                if (spuInfoVo != null) {
                    orderItemEntity.setSpuId(spuInfoVo.getId());//spuId
                    orderItemEntity.setCategoryId(spuInfoVo.getCatalogId());//商品分类id
                    orderItemEntity.setSpuName(spuInfoVo.getSpuName());//spu名称
                }
                return orderItemEntity;
            }).collect(Collectors.toList());
            orderCreateVo.setOrderItemEntities(orderItemEntitys);
            //3、将订单和订单项保存入数据库
            orderEntity.setCreateTime(new Date());
            orderEntity.setModifyTime(new Date());
            orderItemService.saveBatch(orderItemEntitys);//订单项集合批量存到数据库
            this.save(orderEntity);//订单存到数据库
            //4、保存数据库成功后锁定ware商品库存
            List<SkuAndNumVo> skuAndNumVos = item.stream().map(ii -> {//skuId和该商品的数量
                SkuAndNumVo skuAndNumVo = new SkuAndNumVo();
                skuAndNumVo.setNum(ii.getCount());
                skuAndNumVo.setSkuId(ii.getSkuId());
                return skuAndNumVo;
            }).collect(Collectors.toList());
            Boolean aBoolean = wmsFeignServcie.lockOrder(skuAndNumVos, orderEntity.getOrderSn());
            if (aBoolean == true)
                resultOrderSubmitVo.setResultCode(0);//设定成功码0
            else {
                resultOrderSubmitVo.setResultCode(3);//3代表锁定库存失败
                throw new SkuWareLockException(resultOrderSubmitVo);
            }
        } else {//lua脚本失败执行
            resultOrderSubmitVo.setResultCode(1);//1代表lua脚本执行失败
        }
        return resultOrderSubmitVo;
    }


    @Override
    public OrderConfirmVo getOrderConfirmVo() {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        //1\远程调用member服务获取用户地址
        String username = (String) AuthInterceptor.th.get();
        if (username != null) {
            List<MemberReceiveAddress> addresses = memberFeignService.addresses(username);
            if (addresses != null) {
                orderConfirmVo.setAddress(addresses);
            }
        }
        //2\远程调用cart服务获取checked的购物项，cart服务需要远程调用product模块获得sku最新价格
        List<OrderItemVo> item = cartFeignService.item();
        if (item != null) {
            orderConfirmVo.setItems(item);
        }
        //3\远程调用member服务获取用户积分
        orderConfirmVo.setIntegration(100);//这里我们默认每个用户默认100积分
        //4\根据购物项计算出订单总价以及最终总价格
        //5\远程调用ware服务查询是否拥有库存
        List<Long> skuIdList = item.stream().map(e -> {
            return e.getSkuId();
        }).collect(Collectors.toList());
        if (skuIdList != null) {
            List<HasStockVo> stockNums = wmsFeignServcie.hasTock(skuIdList);
            Map<Long, Boolean> hasStock = stockNums.stream().collect(Collectors.toMap(e -> e.getSkuId(), e -> e.getHastock()));
            orderConfirmVo.setHasStock(hasStock);
        }
        //6\为redis和页面都加上订单防重复令牌
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        redisTemplate.opsForValue().set("order:token:" + username, token);
        orderConfirmVo.setToken(token);
        return orderConfirmVo;
    }

    @Transactional
    @Override//mq延迟队列检查订单状态，进行超时关单
    public void checkOrderStatus(String orderSn) {
        OrderEntity orderEntityBySn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        if (orderEntityBySn.getStatus() != 0) {//如果订单的状态不是0，则说明订单已经支付或取消了
            return;
        }//如果此刻程序已经判断支付超时，需要关闭订单，而用户却完成支付，会导致极为严重的问题，但是这种情况基本上不可能发生
        orderEntityBySn.setStatus(5);//设置5表示无效订单
        this.updateById(orderEntityBySn);//支付超时关闭订单确认
        //如果订单还是未支付状态，则远程调用ware服务，将锁定的库存全部解锁
        List<OrderItemEntity> orderItemEntityBySn = orderItemService
                .list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        List<SkuAndNumVo> skuAndNumUnlockOrder = orderItemEntityBySn.stream().map(e -> {
            SkuAndNumVo skuAndNumVo = new SkuAndNumVo();
            skuAndNumVo.setSkuId(e.getSkuId());
            skuAndNumVo.setNum(e.getSkuQuantity());
            return skuAndNumVo;
        }).collect(Collectors.toList());
        SkuAndNumVoList skuAndNumVoList = new SkuAndNumVoList();
        skuAndNumVoList.setOrderSn(orderSn);
        skuAndNumVoList.setSkuAndNumUnlockOrder(skuAndNumUnlockOrder);
        rocketMQTemplate.convertAndSend("ware-delay", skuAndNumVoList);
    }


    @Override
    public String orderPageShow(Map<String, Object> params) {
        String username = (String) AuthInterceptor.th.get();
        //先分页把目标订单列表查出来
        IPage<OrderEntity> member_username = this.page(new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_username", username));
        List<OrderEntity> records = member_username.getRecords();
        List<OrderEntity> orderEntities = records.stream().map(e -> {
            List<OrderItemEntity> orderItemEntities = orderItemService
                    .list(new QueryWrapper<OrderItemEntity>().eq("order_sn", e.getOrderSn()));
            e.setOrderItemEntities(orderItemEntities);
            return e;
        }).collect(Collectors.toList());
        member_username.setRecords(orderEntities);
        PageUtils pageUtils = new PageUtils(member_username);
        String pageUtilsJson = JSON.toJSONString(pageUtils);
        return pageUtilsJson;
    }

    @Override
    public PayVo getOrderInfo(String orderSn) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        OrderEntity orderEntity = this.getOne(queryWrapper
                .and(qw -> qw.eq("order_sn", orderSn).eq("status", 0)));
        if (orderEntity == null) {
            return null;
        }
        PayVo payVo = new PayVo();
        payVo.setBody("订单备注");//设置订单备注
        //setScale(2,BigDecimal.ROUND_UP)方法可以让小数保留两位，BigDecimal.ROUND_UP可以自动进位
        payVo.setTotal_amount(orderEntity.getTotalAmount().setScale(2, BigDecimal.ROUND_UP).toString());
        payVo.setSubject(orderEntity.getMemberUsername());
        payVo.setOut_trade_no(orderSn);
        return payVo;
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),//获取分页数据
                new QueryWrapper<OrderEntity>()
        );
        return new PageUtils(page);
    }
}