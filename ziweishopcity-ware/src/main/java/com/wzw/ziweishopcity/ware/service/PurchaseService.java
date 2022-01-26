package com.wzw.ziweishopcity.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wzw.common.utils.PageUtils;
import com.wzw.ziweishopcity.ware.entity.PurchaseEntity;
import com.wzw.ziweishopcity.ware.vo.DoneVo;
import com.wzw.ziweishopcity.ware.vo.MergeVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author wangziwei
 * @email 937327493@qq.com
 * @date 2021-11-30 21:02:23
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    void done(DoneVo doneVo);

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnReceive(Map<String, Object> params);

    void mergePruchese(MergeVo mergeVo);

    void receive(List<Long> ids);

}

