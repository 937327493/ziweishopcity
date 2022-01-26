package com.wzw.ziweishopcity.member.dao;

import com.wzw.ziweishopcity.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:55:40
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    Integer checkRegistPhoneExist(@Param("phone") String phone);

    Integer checkRegistPasswordExist(@Param("username") String username);
}
