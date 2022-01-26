package com.wzw.ziweishopcity.product.service.impl;

import com.mysql.cj.util.StringUtils;
import com.wzw.common.constant.ProductConstant;
import com.wzw.ziweishopcity.product.dao.AttrAttrgroupRelationDao;
import com.wzw.ziweishopcity.product.dao.AttrGroupDao;
import com.wzw.ziweishopcity.product.dao.CategoryDao;
import com.wzw.ziweishopcity.product.entity.AttrAttrgroupRelationEntity;
import com.wzw.ziweishopcity.product.entity.AttrGroupEntity;
import com.wzw.ziweishopcity.product.entity.CategoryEntity;
import com.wzw.ziweishopcity.product.service.CategoryService;
import com.wzw.ziweishopcity.product.vo.AttrGroupRelationVo;
import com.wzw.ziweishopcity.product.vo.AttrRespVo;
import com.wzw.ziweishopcity.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.AttrDao;
import com.wzw.ziweishopcity.product.entity.AttrEntity;
import com.wzw.ziweishopcity.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    CategoryDao categoryDao;
    @Autowired
    CategoryService categoryService;
    @Autowired
    AttrDao attrDao;
    @Autowired
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    AttrGroupDao attrGroupDao;

    //获取当前分组还没有关联的基本属性,而且这个基本属性还不能绑定任何属性分组
    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        //1、得到该分组所属的商品分类
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //2、得到商品分类下的所有属性分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao
                .selectList(new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId));
        //3、该分类的所有基本属性中去除这些属性分组已经关联的基本属性
        //3.1、查询该分类的所有基本属性
//        List<AttrEntity> attrEntities = attrDao.selectList(new QueryWrapper<AttrEntity>()
//                .eq("catelog_id", catelogId)
//                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()));
        //3.2、查询商品分类下的所有属性分组已经关联的基本属性
        List<Long> collect = attrGroupEntities.stream().flatMap(e -> {
            return attrAttrgroupRelationDao
                    .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", e.getAttrGroupId())
                    ).stream().map(c -> {
                        return attrDao.selectOne(new QueryWrapper<AttrEntity>().eq("attr_id", c.getAttrId()))
                                .getAttrId();
                    });
        }).collect(Collectors.toList());
        //3.3、将该分类的所有基本属性中去除这些属性分组已经关联的基本属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", catelogId);
        if (collect != null && collect.size() > 0) {
            queryWrapper.notIn("attr_id", collect);
        }
        queryWrapper.ne("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        //4、如果前端的分页请求携带了key，我们要进行处理
        String key = (String) params.get("key");
        if (!StringUtils.isNullOrEmpty(key)) {
            queryWrapper.and(obj -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }


    @Override
    public List<AttrRespVo> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntitys =
                attrAttrgroupRelationDao
                        .selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_group_id", attrgroupId));
        List<AttrRespVo> attrRespVos = new ArrayList<>();
        List<AttrRespVo> attr_id = attrAttrgroupRelationEntitys.stream()
                .map(e -> {
                    AttrEntity attrEntity = attrDao
                            .selectOne(new QueryWrapper<AttrEntity>()
                                    .eq("attr_id", e.getAttrId()));
                    AttrRespVo attrRespVo = new AttrRespVo();
                    BeanUtils.copyProperties(attrEntity, attrRespVo);
                    attrRespVo.setAttrGroupId(e.getAttrGroupId());
                    return attrRespVo;
                }).collect(Collectors.toList());
        return attr_id;
    }


    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //1、先查出基本信息
        AttrEntity attrEntity = attrDao.selectById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        //2、根据catelogId查出三级路径
        Long[] catelogPath = categoryService.findCatelogPath(attrRespVo.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);
        //3、从中间表查出attr_group_id
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attr_id = attrAttrgroupRelationDao
                    .selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrRespVo.getAttrId()));
            attrRespVo.setAttrGroupId(attr_id.getAttrGroupId());
        }
        return attrRespVo;
    }


    @Override
    public PageUtils getBaseAttrList(Long catelogId, Map<String, Object> params, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        //如果uri是base则必定是基本属性，数据库中是1
        queryWrapper.eq("attr_type", "base".equalsIgnoreCase(type) ? 1 : 0);
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (!StringUtils.isNullOrEmpty(key)) {
            queryWrapper.and(obj -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> collect = records.stream().map(e -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(e, attrRespVo);
            //1、通过查询属性和属性分组关联表+属性分组表得到属性分组名称
//            只有基本属性才拥有属性分组
            if ("base".equalsIgnoreCase(type)) {
                QueryWrapper<AttrAttrgroupRelationEntity> objectQueryWrapper = new QueryWrapper<>();
                objectQueryWrapper.eq("attr_id", attrRespVo.getAttrId());
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntitie =
                        attrAttrgroupRelationDao.selectOne(objectQueryWrapper);
                if (attrAttrgroupRelationEntitie != null && attrAttrgroupRelationEntitie.getAttrGroupId() != null) {
                    attrRespVo.setAttrGroupId(attrAttrgroupRelationEntitie.getAttrGroupId());
                    AttrGroupEntity attrGroupEntity1 = attrGroupDao
                            .selectOne(new QueryWrapper<AttrGroupEntity>()
                                    .eq("attr_group_id", attrAttrgroupRelationEntitie.getAttrGroupId()));
                    attrRespVo.setGroupName(attrGroupEntity1.getAttrGroupName());
                }
            }
            //2、通过查询商品分类表得到商品分类名称
            CategoryEntity categoryEntity = categoryDao.selectById(attrRespVo.getCatelogId());
            attrRespVo.setCatelogName(categoryEntity.getName());
            return attrRespVo;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(collect);
        return pageUtils;
    }


    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        //1、保存基本数据
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        //2、保存关联数据，销售属性不需要保存属性分组关联数据
        if (attrEntity.getAttrType() == 1) {
            attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
            attrAttrgroupRelationDao.insert(attrAttrgroupRelationEntity);
        }
    }



    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }


}