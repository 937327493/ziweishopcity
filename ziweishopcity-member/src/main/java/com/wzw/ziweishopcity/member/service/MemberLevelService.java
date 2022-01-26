package com.wzw.ziweishopcity.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 20:55:40
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

