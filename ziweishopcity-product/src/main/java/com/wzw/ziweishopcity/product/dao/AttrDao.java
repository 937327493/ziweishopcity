package com.wzw.ziweishopcity.product.dao;

import com.wzw.ziweishopcity.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 商品属性
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:00:51
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {
	
}
