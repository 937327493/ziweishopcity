package com.wzw.ziweishopcity.coupon.dao;

import com.wzw.ziweishopcity.coupon.entity.HomeSubjectEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 22:09:51
 */
@Mapper
public interface HomeSubjectDao extends BaseMapper<HomeSubjectEntity> {
	
}
