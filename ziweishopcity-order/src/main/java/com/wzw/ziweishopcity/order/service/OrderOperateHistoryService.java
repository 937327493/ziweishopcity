package com.wzw.ziweishopcity.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:58:42
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

