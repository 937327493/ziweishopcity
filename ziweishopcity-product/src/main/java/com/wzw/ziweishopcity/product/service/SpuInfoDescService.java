package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.SpuInfoDescEntity;
import com.wzw.ziweishopcity.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-12-19 15:19:59
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDecriptSpuInfo(SpuInfoDescEntity spuInfoDescEntity);
}

