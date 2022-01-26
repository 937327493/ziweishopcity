package com.wzw.ziweishopcity.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.member.entity.MemberEntity;
import com.wzw.ziweishopcity.member.exception.PhoneExistException;
import com.wzw.ziweishopcity.member.exception.UsernameExistException;
import com.wzw.ziweishopcity.member.vo.MemberLoginVo;
import com.wzw.ziweishopcity.member.vo.MemberRegisterVo;
import com.wzw.ziweishopcity.member.vo.SocialLoginResultVo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * 会员
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:55:40
 */
public interface MemberService extends IService<MemberEntity> {
    MemberEntity login(MemberLoginVo memberLoginVo);

    PageUtils queryPage(Map<String, Object> params);

    void registMember(MemberRegisterVo memberRegisterVo);

    void checkRegistPhoneExist(String phone) throws PhoneExistException;

    void checkRegistUsernameExist(String username) throws UsernameExistException;

    MemberEntity handlerSocialLoginMessage(SocialLoginResultVo socialLoginResultVo);
}

