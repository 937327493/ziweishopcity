package com.wzw.ziweishopcity.product.service.impl;

import com.wzw.ziweishopcity.product.vo.SpuItemAttrGroupVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.AttrGroupDao;
import com.wzw.ziweishopcity.product.entity.AttrGroupEntity;
import com.wzw.ziweishopcity.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catId) {
        String s = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> attrGroupEntityQueryWrapper = new QueryWrapper<>();

        if (s != null) {
            attrGroupEntityQueryWrapper.and(obj -> {
                obj.eq("attr_group_id", s).or().like("attr_group_name", s);
            });
        }
        if (catId == 0) {
            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    attrGroupEntityQueryWrapper
            );
            return new PageUtils(page);
        } else {
            attrGroupEntityQueryWrapper.eq("catelog_id", catId);

            IPage<AttrGroupEntity> page = this.page(
                    new Query<AttrGroupEntity>().getPage(params),
                    attrGroupEntityQueryWrapper
            );
            return new PageUtils(page);
        }
    }

    @Override
    public List<SpuItemAttrGroupVo> getGroupAttrBySpuIdAndCatalogId(Long spuId, Long catalogId) {
        AttrGroupDao baseMapper = this.getBaseMapper();
        List<SpuItemAttrGroupVo> spuItemAttrGroupVos = baseMapper.getGroupAttrBySpuIdAndCatalogId(spuId,catalogId);
        return spuItemAttrGroupVos;
    }

}