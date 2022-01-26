package com.wzw.ziweishopcity.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wzw.ziweishopcity.seckill.service.SickService;
import com.wzw.ziweishopcity.seckill.vo.SeckillRedisTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class SeckillController {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    SickService sickService;

    /**
     *
     * @param num 秒杀数量
     * @param key 秒杀随机码
     * @param killId sessionId_skuId
     * @return
     */
    @GetMapping("/seckill/go")
    public String seckillGo(@RequestParam("num") String num,
                            @RequestParam("key") String key,
                            @RequestParam("killId") String killId, Model model) {
        //拿到用户秒杀信息后需要进行校验，，如果校验全部通过，返回给用户订单号，如果不通过返回秒杀失败信息
        String orderSn = sickService.checkSeckillMessage(num,key,killId);
        model.addAttribute("orderSn",orderSn);
        return "success";
    }

    /**
     * 每个商品，在秒杀上架时都应确保单个商品的场次与场次的时间间隔大于三天，因为从redis中取秒杀商品信息时，需要循环匹配key值，
     * 应该保证唯一循环结束都取不到则返回null
     *
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/productGetSeckillRedisTo/{skuId}")
    public SeckillRedisTo productGetSeckillRedisTo(@PathVariable("skuId") String skuId) {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps("seckill:skus");
        Set<String> keys = boundHashOperations.keys();
        SeckillRedisTo seckillRedisTo = null;
        for (String key : keys) {
            String zz = "[\\d]+_" + skuId;
            if (key.matches(zz)) {
                String skuJson = boundHashOperations.get(key);
                seckillRedisTo = JSON.parseObject(skuJson, SeckillRedisTo.class);
                long time = new Date().getTime();
                if (Long.parseLong(seckillRedisTo.getStartTime()) <= time &&
                        Long.parseLong(seckillRedisTo.getEndTime()) >= time) {
                } else {
                    seckillRedisTo.setRandomKey(null);
                }
                return seckillRedisTo;
            }
        }
        return seckillRedisTo;
    }

    /**
     * 接口可以返回相关场次的所有秒杀信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/seckillSkuInfo")
    public List<SeckillRedisTo> getSeckillSkuInfo() {
        String sessionKey = getSessionKey();
        if (!StringUtils.isNotEmpty(sessionKey)) {
            return null;
        }
        //拿到当前场次所有的sessionid_skuId
        String range = (String) redisTemplate.opsForList().range(sessionKey, -100, 100).get(0);
        List<String> list = JSON.parseObject(range, new TypeReference<List<String>>() {
        });
        List<SeckillRedisTo> seckillRedisTos = getSeckillRedisTos(list);
        return seckillRedisTos;
    }

    private List<SeckillRedisTo> getSeckillRedisTos(List<String> range) {
        BoundHashOperations<String, String, String> boundHashOperations = redisTemplate.boundHashOps("seckill:skus");
        List<String> list = boundHashOperations.multiGet(range);
        List<SeckillRedisTo> collect = list.stream().map(item -> {
            SeckillRedisTo seckillRedisTo = JSON.parseObject(item, SeckillRedisTo.class);
            seckillRedisTo.setRandomKey(null);//不要把秒杀的随机码暴露给前端用户,起始返回的就是当前场次的秒杀商品，暴露随机码也无所谓
            return seckillRedisTo;
        }).collect(Collectors.toList());
        return collect;
    }

    private String getSessionKey() {
        long time = new Date().getTime();
        Set keys = redisTemplate.keys("seckill:session:*");
        if (keys != null) {
            for (Object key : keys) {
                String s = key.toString();
                String sReplace = s.replaceAll("seckill:session:", "");
                String[] timeArray = sReplace.split("_");
                long lstart = Long.parseLong(timeArray[0]);
                long lend = Long.parseLong(timeArray[1]);
                if (lstart <= time && time <= lend) {
                    return s;
                }
            }
        }
        return null;
    }
}
