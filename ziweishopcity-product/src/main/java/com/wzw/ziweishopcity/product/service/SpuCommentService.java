package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-12-19 15:19:59
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

