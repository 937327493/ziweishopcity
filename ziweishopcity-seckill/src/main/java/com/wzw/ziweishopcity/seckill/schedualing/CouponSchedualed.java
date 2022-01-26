package com.wzw.ziweishopcity.seckill.schedualing;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wzw.common.utils.R;
import com.wzw.ziweishopcity.seckill.feign.ProductFeign;
import com.wzw.ziweishopcity.seckill.service.SickService;
import com.wzw.ziweishopcity.seckill.vo.SeckillRedisTo;
import com.wzw.ziweishopcity.seckill.vo.SeckillSessionEntityVo;
import com.wzw.ziweishopcity.seckill.vo.SeckillSkuRelationEntityVo;
import com.wzw.ziweishopcity.seckill.vo.SkuInfoVo;
import io.swagger.models.auth.In;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@EnableAsync
@EnableScheduling
public class CouponSchedualed {
    @Autowired
    SickService sickService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    ProductFeign productFeign;
    @Autowired
    RedissonClient redissonClient;

    final String SECKILLSESSION = "seckill:session:";
    final String SECKILLSKUS = "seckill:skus";
    final String SECKILLSTOCK = "seckill:stock:";
    final String SECKILLEQUALSESSIONALL = "seckill:upload";//场次数据幂等性


    /**
     * 秒杀场次起始时间终止时间和对应的所有sessionid_skuid放进redis  seckill:session:开始时间_结束时间
     * seckill:skus 、 sessionid_skuid 、sku详细信息放进redis   seckill:skus
     * 信号量存储进redis   随机码+数量(随机码可以代表一个商品)  seckill:stock:随机码
     */
    @Async
    @Scheduled(cron = "*/10 * * * * ?")//每天凌晨三点执行定时任务，并且异步执行
    public void getSeckillPromotion() {
        RLock lock = redissonClient.getLock(SECKILLEQUALSESSIONALL);
        lock.lock(20, TimeUnit.SECONDS);
        try {
            R promotion3day = sickService.getPromotion3day();
            if ((Integer) promotion3day.get("code") != 0)
                return;
            String session = (String) promotion3day.get("session");
            List<SeckillSessionEntityVo> seckillSessionEntityVos = JSON.parseObject(session, new TypeReference<List<SeckillSessionEntityVo>>() {
            });
            saveSessionInRedis(seckillSessionEntityVos);
            saveAllSeckillSkuInRedis(seckillSessionEntityVos);
        } finally {
            lock.unlock();
        }
    }

    private void saveAllSeckillSkuInRedis(List<SeckillSessionEntityVo> seckillSessionEntityVos) {
        //2、将seckill:skus 、 skuid 、sku信息放进redis  HASH
        BoundHashOperations boundHashOperations = redisTemplate.boundHashOps(SECKILLSKUS);//批量操作hash
        List<List<SeckillRedisTo>> collect2 = seckillSessionEntityVos
                .stream()
                .map(e -> {
                    List<SeckillRedisTo> collect1 = e.getSeckillSkuRelationEntity().stream().map(d -> {
                        SeckillRedisTo seckillRedisTo = new SeckillRedisTo();
                        if (!boundHashOperations.hasKey(d.getPromotionSessionId().toString() + "_" + d.getSkuId().toString())) {
                            //秒杀商品信息进行对拷
                            BeanUtils.copyProperties(d, seckillRedisTo);
                            long startTime = e.getStartTime().getTime();
                            long endTime = e.getEndTime().getTime();
                            //存储起始终止时间
                            seckillRedisTo.setStartTime(startTime + "");
                            seckillRedisTo.setEndTime(endTime + "");
                            //2.1 产生随机码
                            String randomKey = UUID.randomUUID().toString().replaceAll("-", "");
                            seckillRedisTo.setRandomKey(randomKey);
                            //2.2远程product服务获取sku商品信息
                            R skuInfo = productFeign.getSkuInfo(d.getSkuId());
                            SkuInfoVo skuInfoVo = null;
                            if ((Integer) skuInfo.get("code") == 0) {
                                String json = null;
                                json = (String) skuInfo.get("skuInfo");
                                skuInfoVo = JSON.parseObject(json, SkuInfoVo.class);
                            }
                            seckillRedisTo.setSkuInfoVo(skuInfoVo);
                            String jsonString = JSON.toJSONString(seckillRedisTo);
                            boundHashOperations.put(d.getPromotionSessionId().toString() + "_" + d.getSkuId().toString(), jsonString);
                            //存入redis
                            //3、将信号量存储进redis   随机码+数量(随机码可以代表一个商品)  STRING
                            RSemaphore semaphore = redissonClient.getSemaphore(SECKILLSTOCK + seckillRedisTo.getRandomKey());
                            semaphore.trySetPermits(d.getSeckillCount().intValue());
                        }
                        return seckillRedisTo;
                    }).collect(Collectors.toList());
                    return collect1;
                }).collect(Collectors.toList());
    }

    private void saveSessionInRedis(List<SeckillSessionEntityVo> seckillSessionEntityVos) {
        //1、将开始时间_结束时间和对应的所有skuid放进redis  LIST
        List<String> collect = seckillSessionEntityVos.stream()
                .map(e -> {
                    long startTime = e.getStartTime().getTime();
                    long endTime = e.getEndTime().getTime();
                    String key = SECKILLSESSION + Long.toString(startTime) + "_" + Long.toString(endTime);
                    if (!redisTemplate.hasKey(key)) {
                        List<String> seckillSkuRelationEntity = e.getSeckillSkuRelationEntity().stream().map(d -> {
                            Long skuId = d.getSkuId();
                            return e.getId().toString() + "_" + skuId.toString();
                        }).collect(Collectors.toList());
                        String jsonString = JSON.toJSONString(seckillSkuRelationEntity);
                        redisTemplate.opsForList().leftPushAll(key, jsonString);//sessionid_skuid
                    }
                    return key;
                }).collect(Collectors.toList());
    }
}
