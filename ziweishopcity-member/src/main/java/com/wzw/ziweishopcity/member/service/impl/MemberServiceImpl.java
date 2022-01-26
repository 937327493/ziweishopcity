package com.wzw.ziweishopcity.member.service.impl;

import com.wzw.ziweishopcity.member.dao.MemberLevelDao;
import com.wzw.ziweishopcity.member.entity.MemberLevelEntity;
import com.wzw.ziweishopcity.member.exception.PhoneExistException;
import com.wzw.ziweishopcity.member.exception.UsernameExistException;
import com.wzw.ziweishopcity.member.vo.MemberLoginVo;
import com.wzw.ziweishopcity.member.vo.MemberRegisterVo;
import com.wzw.ziweishopcity.member.vo.SocialLoginResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wzw.common.utils.PageUtils;
import com.wzw.common.utils.Query;

import com.wzw.ziweishopcity.member.dao.MemberDao;
import com.wzw.ziweishopcity.member.entity.MemberEntity;
import com.wzw.ziweishopcity.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public MemberEntity handlerSocialLoginMessage(SocialLoginResultVo socialLoginResultVo){
        MemberDao baseMapper = this.getBaseMapper();
        MemberEntity socialUser = baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("social_Uid", socialLoginResultVo.getUid()));
        //如果数据库里已经有该社交用户，则更新其令牌和令牌过期时间
        MemberEntity memberEntity = null;
        if (socialUser != null) {
            memberEntity = new MemberEntity();
            memberEntity.setExpiresIn(socialLoginResultVo.getExpires_in());
            memberEntity.setAccessToken(socialLoginResultVo.getAccess_token());
            memberEntity.setId(socialUser.getId());
            baseMapper.updateById(memberEntity);
            memberEntity.setUsername(socialUser.getUsername());
            return memberEntity;
        } else {//如果数据库里没有该社交用户，插入新的一行用户信息
            memberEntity = new MemberEntity();
            memberEntity.setExpiresIn(socialLoginResultVo.getExpires_in());
            memberEntity.setAccessToken(socialLoginResultVo.getAccess_token());
            memberEntity.setSocialUid(socialLoginResultVo.getUid());
            memberEntity.setUsername(socialLoginResultVo.getUid());//就用户uid作为社交登录的用户名
            baseMapper.insert(memberEntity);
            return memberEntity;
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo memberLoginVo) {
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>()
                .eq("username", memberLoginVo.getUsername())
                .or().eq("mobile", memberLoginVo.getUsername()));
        if (memberEntity == null) {
            return null;
        } else {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            boolean matches = bCryptPasswordEncoder.matches(memberLoginVo.getPassword(), memberEntity.getPassword());
            if (matches == true) {
                return memberEntity;
            } else
                return null;
        }
    }

    @Override
    public void registMember(MemberRegisterVo memberRegisterVo) {
        MemberDao baseMapper = this.getBaseMapper();
        //检查是否注册过,没报异常就可以继续
        checkRegistPhoneExist(memberRegisterVo.getPhone());
        checkRegistUsernameExist(memberRegisterVo.getUsername());
        //封装po数据
        MemberEntity memberEntity = new MemberEntity();
        //1\获取默认会员等级id，绑定
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultMemberlevelId();
        if (memberLevelEntity != null)
            memberEntity.setLevelId(memberLevelEntity.getId());
        //2\将手机号绑定
        memberEntity.setMobile(memberRegisterVo.getPhone());
        //3\将用户名绑定
        memberEntity.setUsername(memberRegisterVo.getUsername());
        //4\密码需要加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodePwd = bCryptPasswordEncoder.encode(memberRegisterVo.getPassword());
        memberEntity.setPassword(encodePwd);
        //将数据插入
        baseMapper.insert(memberEntity);
    }

    @Override
    public void checkRegistPhoneExist(String phone) throws PhoneExistException {
        MemberDao baseMapper = this.getBaseMapper();
        Integer integer = baseMapper.checkRegistPhoneExist(phone);
        if (integer > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkRegistUsernameExist(String username) throws UsernameExistException {
        MemberDao baseMapper = this.getBaseMapper();
        Integer integer = baseMapper.checkRegistPasswordExist(username);
        if (integer > 0) {
            throw new UsernameExistException();
        }
    }


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );
        return new PageUtils(page);
    }
}