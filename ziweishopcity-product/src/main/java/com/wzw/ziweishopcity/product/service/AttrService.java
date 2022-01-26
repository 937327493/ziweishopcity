package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.AttrEntity;
import com.wzw.ziweishopcity.product.vo.AttrGroupRelationVo;
import com.wzw.ziweishopcity.product.vo.AttrRespVo;
import com.wzw.ziweishopcity.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils getBaseAttrList(Long catelogId, Map<String, Object> params,String type);

    AttrRespVo getAttrInfo(Long attrId);

    List<AttrRespVo> getRelationAttr(Long attrgroupId);

    PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params);

}

