package com.wzw.ziweishopcity.seckill.service;

import com.wzw.common.utils.R;

public interface SickService {
    public R getPromotion3day();

    String checkSeckillMessage(String num, String key, String killId);
}
