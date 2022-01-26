package com.wzw.ziweishopcity.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.ware.entity.WareSkuEntity;
import com.wzw.common.to.StockNum;
import com.wzw.ziweishopcity.ware.vo.SkuAndNumVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:02:23
 */
public interface WareSkuService extends IService<WareSkuEntity> {
    void unlockOrder(List<SkuAndNumVo> skuAndNumUnlockOrder,String orderSn);

    Boolean lockOrder(List<SkuAndNumVo> skuAndNumVos,String orderSn);

    List<StockNum> hasTock(List<Long> skuId);

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCondition(Map<String, Object> params);

}

