package com.wzw.ziweishopcity.seckill.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 秒杀活动场次
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 22:09:51
 */
@Data
public class SeckillSessionEntityVo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    /**
     * 场次名称
     */
    private String name;
    /**
     * 每日开始时间
     */
    private Date startTime;
    /**
     * 每日结束时间
     */
    private Date endTime;
    /**
     * 启用状态
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;

    private List<SeckillSkuRelationEntityVo> seckillSkuRelationEntity;
}
