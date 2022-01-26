package com.wzw.ziweishopcity.product.service.impl;

import com.wzw.ziweishopcity.product.entity.SpuInfoEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.product.dao.SpuInfoDescDao;
import com.wzw.ziweishopcity.product.entity.SpuInfoDescEntity;
import com.wzw.ziweishopcity.product.service.SpuInfoDescService;


@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescDao, SpuInfoDescEntity> implements SpuInfoDescService {

    @Override
    public void saveDecriptSpuInfo(SpuInfoDescEntity spuInfoDescEntity) {
        this.baseMapper.insert(spuInfoDescEntity);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoDescEntity> page = this.page(
                new Query<SpuInfoDescEntity>().getPage(params),
                new QueryWrapper<SpuInfoDescEntity>()
        );

        return new PageUtils(page);
    }



}