package com.wzw.ziweishopcity.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.seckill.feign.CouponFeign;
import com.wzw.ziweishopcity.seckill.interceptor.SeckillInterceptor;
import com.wzw.ziweishopcity.seckill.service.SickService;
import com.wzw.ziweishopcity.seckill.vo.SeckillOrderVo;
import com.wzw.ziweishopcity.seckill.vo.SeckillRedisTo;
import jodd.time.TimeUtil;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.redisson.connection.IdleConnectionWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class SickServiceImpl implements SickService {
    @Autowired
    CouponFeign couponFeign;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Override
    public R getPromotion3day() {
        //远程调用Coupon服务获取秒杀场次以及秒杀skuid
        R promotion3day = couponFeign.getPromotion3day();
        return promotion3day;
    }

    @Override
    public String checkSeckillMessage(String num, String key, String killId) {
        String username = (String) SeckillInterceptor.th.get();
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps("seckill:skus");
        String redisProduct = boundHashOperations.get(killId);
        SeckillRedisTo seckillRedisTo = JSON.parseObject(redisProduct, SeckillRedisTo.class);
        //1、校验秒杀时间正确且秒杀数量正确
        String startTime = seckillRedisTo.getStartTime();
        String endTime = seckillRedisTo.getEndTime();
        long time = new Date().getTime();
        long expireTime = Long.parseLong(endTime) - time;
        int number = Integer.parseInt(num);
        if (time >= Long.parseLong(startTime) && time <= Long.parseLong(endTime) && number <= seckillRedisTo.getSeckillLimit().intValue()) {
            //2、校验秒杀商品的随机码和用户发来的随机码匹配
            String randomKey = seckillRedisTo.getRandomKey();
            if (randomKey.equals(key)) {
                //3、校验用户是第一次进行该商品该场次的秒杀
                Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(username + "_" + killId, key, expireTime, TimeUnit.MILLISECONDS);
                if (aBoolean) {
                    //4、校验秒杀商品未卖空
                    RSemaphore semaphore = redissonClient.getSemaphore("seckill:stock:" + key);
                    boolean acquireResult = semaphore.tryAcquire();
                    if (acquireResult) {
                        //5、四重校验后将秒杀信息放入RocketMQ供订单服务进行创建订单操作并返回订单号
                        String orderSn = IdWorker.getTimeId();
                        SeckillOrderVo seckillOrderVo = new SeckillOrderVo();
                        seckillOrderVo.setOrderSn(orderSn);
                        seckillOrderVo.setSeckillPrice(seckillRedisTo.getSeckillPrice().multiply(new BigDecimal(num)));
                        seckillOrderVo.setNum(Integer.parseInt(num));
                        seckillOrderVo.setSkuId(seckillRedisTo.getSkuId());
                        seckillOrderVo.setPromotionSessionId(seckillRedisTo.getPromotionSessionId());
                        seckillOrderVo.setUsername(username);
                        rocketMQTemplate.convertAndSend("seckill-order", seckillOrderVo);
                        return orderSn;
                    }
                }
            }
        }
        return null;
    }
}
