package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.SpuInfoEntity;
import com.wzw.ziweishopcity.product.vo.SpuSaveVo;

import java.util.List;
import java.util.Map;

/**
 * spu信息
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-12-19 15:19:59
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {
    void upToShop(Long spuId);

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo spuSaveVo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

}

