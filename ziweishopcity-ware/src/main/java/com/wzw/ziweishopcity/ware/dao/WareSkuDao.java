package com.wzw.ziweishopcity.ware.dao;

import com.wzw.ziweishopcity.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzw.ziweishopcity.ware.vo.SkuAndNumVo;
import com.wzw.ziweishopcity.ware.vo.SkuAndWareAndNumVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:02:23
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void unlockOrder(@Param("skuId") Long skuId, @Param("num") Integer num, @Param("wareId") Long wareId);


    Integer lockStock(@Param("num") Integer num, @Param("skuId") Long skuId, @Param("wareId") Long wareId);

    List<Long> getWareIds(Long skuId);

    Long getStock(Long aLong);

}
