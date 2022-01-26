package com.wzw.ziweishopcity.order.feign;

import com.wzw.ziweishopcity.order.vo.MemberReceiveAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("ziweishopcity-member")
public interface MemberFeignService {
    @GetMapping("/member/memberreceiveaddress/{username}/addresses")
    public List<MemberReceiveAddress> addresses(@PathVariable("username") String username);
}
