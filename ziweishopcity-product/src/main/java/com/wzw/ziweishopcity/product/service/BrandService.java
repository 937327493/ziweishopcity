package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

