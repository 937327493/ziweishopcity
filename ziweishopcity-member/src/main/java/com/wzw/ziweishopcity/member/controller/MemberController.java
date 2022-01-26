package com.wzw.ziweishopcity.member.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

import com.wzw.ziweishopcity.member.exception.PhoneExistException;
import com.wzw.ziweishopcity.member.exception.UsernameExistException;
import com.wzw.ziweishopcity.member.vo.MemberLoginVo;
import com.wzw.ziweishopcity.member.vo.MemberRegisterVo;
import com.wzw.ziweishopcity.member.vo.SocialLoginResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.wzw.ziweishopcity.member.entity.MemberEntity;
import com.wzw.ziweishopcity.member.service.MemberService;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.R;


/**
 * 会员
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:55:40
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/socialLogin")
    public R socialLogin(@RequestBody SocialLoginResultVo socialLoginResultVo){
        MemberEntity memberEntity = memberService.handlerSocialLoginMessage(socialLoginResultVo);
        if (memberEntity != null){
            R socialUser = R.ok().put("socialUser", memberEntity);
            return socialUser;
        }else
            return R.error();
    }

    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo memberLoginVo){
        MemberEntity memberEntity = memberService.login(memberLoginVo);
        if (memberEntity == null){
            return R.error(500,"不存在该用户或密码错误");
        }else{
            return R.ok().put("loginUser",memberEntity);
        }
    }


    @PostMapping("/register")
    public R regist(@RequestBody MemberRegisterVo memberRegisterVo) {
        try {
            memberService.registMember(memberRegisterVo);
        } catch (PhoneExistException e) {
            System.out.println(e.getMessage());
            return R.error("手机号已注册");//500
        }catch (UsernameExistException g){
            System.out.println(g.getMessage());
            return R.error("用户名已注册");//500
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
