package com.wzw.ziweishopcity.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wzw.ziweishopcity.product.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.AttrAttrgroupRelationDao;
import com.wzw.ziweishopcity.product.entity.AttrAttrgroupRelationEntity;
import com.wzw.ziweishopcity.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Override
    public void attrAttrGroupRelation(List<AttrGroupRelationVo> list) {
        List<AttrAttrgroupRelationEntity> collect = list.stream().map(e -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(e, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        this.saveBatch(collect);
    }
    @Override
    public void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVo) {
        for (AttrGroupRelationVo groupRelationVo : attrGroupRelationVo) {
            attrAttrgroupRelationDao.delete(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id",groupRelationVo.getAttrId())
                    .eq("attr_group_id",groupRelationVo.getAttrGroupId()));
        }
    }




    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }



}