package com.wzw.ziweishopcity.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.product.entity.AttrAttrgroupRelationEntity;
import com.wzw.ziweishopcity.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVo);

    void attrAttrGroupRelation(List<AttrGroupRelationVo> list);
}

