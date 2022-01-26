package com.wzw.ziweishopcity.order.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrderSubmitVo {
    private Long addrId;//地址的id
    private String shouldPayPrice;//应付价格
    private String token;//幂等性令牌
    private String node;//订单的备注

}
