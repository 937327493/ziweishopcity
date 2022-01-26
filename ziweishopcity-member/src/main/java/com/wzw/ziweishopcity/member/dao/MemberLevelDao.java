package com.wzw.ziweishopcity.member.dao;

import com.wzw.ziweishopcity.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级
 * 
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:55:40
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {

    MemberLevelEntity getDefaultMemberlevelId();
}
