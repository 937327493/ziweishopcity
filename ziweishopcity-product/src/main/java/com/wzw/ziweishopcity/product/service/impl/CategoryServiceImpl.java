package com.wzw.ziweishopcity.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wzw.ziweishopcity.product.vo.Catelog2WebVo;
import com.wzw.ziweishopcity.product.vo.Catelog3WebVo;
import jodd.time.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.CategoryDao;
import com.wzw.ziweishopcity.product.entity.CategoryEntity;
import com.wzw.ziweishopcity.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    public CategoryDao  categoryDao;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Override
    public Map<String,List<Catelog2WebVo>> getCategoryRedis(){
        String catelogJson = (String) redisTemplate.opsForValue().get("catelogJson");
        if (StringUtils.isNotEmpty(catelogJson)){
            Map<String, List<Catelog2WebVo>> stringListMap = JSON.parseObject(catelogJson,
                    new TypeReference<Map<String, List<Catelog2WebVo>>>() {
            });
            return stringListMap;
        }
        RLock catelogJson1 = redissonClient.getLock("catelogJson-lock");
        catelogJson1.lock();
        try {
            catelogJson = (String) redisTemplate.opsForValue().get("catelogJson");
            if (StringUtils.isNotEmpty(catelogJson)){
                {
                    Map<String, List<Catelog2WebVo>> stringListMap = JSON.parseObject(catelogJson,
                            new TypeReference<Map<String, List<Catelog2WebVo>>>() {
                            });
                    return stringListMap;
                }
            }
            Map<String, List<Catelog2WebVo>> catlogJson = getCatlogJson();
            String s = JSON.toJSONString(catlogJson);
            redisTemplate.opsForValue().set("catelogJson",s,60*60*24, TimeUnit.SECONDS);
            return catlogJson;
        }catch (Exception e){
            log.error(e.getMessage());
        }finally {
            catelogJson1.unlock();
        }
        return new HashMap<String, List<Catelog2WebVo>>();
    }

    @Override
    public Map<String,List<Catelog2WebVo>> getCatlogJson() {
        //???????????????????????????
        List<CategoryEntity> levelOne = getLevelOne();
        //???????????????????????????????????????????????????????????????
        Map<String, List<Catelog2WebVo>> collect = levelOne.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> twoCategory = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>()
                    .eq("parent_cid", v.getCatId()));
            //?????????????????????id????????????????????????????????????,????????????????????????????????????
            List<Catelog2WebVo> catelog2WebVos = null;
            if (twoCategory != null) {
                catelog2WebVos = twoCategory.stream().map(t -> {
                    List<CategoryEntity> threeNode = categoryDao.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",
                            t.getCatId()));
                    List<Catelog3WebVo> catelog3WebVos = threeNode.stream().map(m -> {
                        Catelog3WebVo catelog3WebVo = new Catelog3WebVo(t.getCatId().toString(), m.getCatId().toString(), m.getName());
                        return catelog3WebVo;
                    }).collect(Collectors.toList());
                    Catelog2WebVo catelog2WebVo = new Catelog2WebVo(v.getCatId().toString(), catelog3WebVos, t.getCatId().toString(),
                            t.getName());
                    return catelog2WebVo;
                }).collect(Collectors.toList());
            }
            return catelog2WebVos;
        }));
        return collect;
    }


    @Override
    public List<CategoryEntity> getLevelOne() {
        List<CategoryEntity> parent_cid = this.baseMapper.selectList
                (new QueryWrapper<CategoryEntity>()
                        .eq("parent_cid", 0));
        return parent_cid;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

//    ?????????Category??????????????????
    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = categoryDao.selectList(null);
        return categoryEntities;
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        ArrayList<Long> longs = new ArrayList<>();
        ArrayList<Long> parentPath = findParentPath(catelogId, longs);
        //????????????????????????????????????????????????Collections??????????????????
        Collections.reverse(parentPath);
        return  parentPath.toArray(new Long[parentPath.size()]);
    }

    public ArrayList<Long> findParentPath(Long catelogId,ArrayList<Long> longs){
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        longs.add(categoryEntity.getCatId());
        if(categoryEntity.getParentCid() != 0){//???id??????0????????????????????????????????????????????????
            return findParentPath(categoryEntity.getParentCid(),longs);
        }
        return longs;
    }
}