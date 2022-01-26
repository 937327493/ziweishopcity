package com.wzw.ziweishopcity.order.dao;

import com.wzw.ziweishopcity.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:58:42
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
