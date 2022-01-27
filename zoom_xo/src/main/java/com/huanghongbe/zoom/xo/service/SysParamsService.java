package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.SysParams;
import com.huanghongbe.zoom.xo.vo.SysParamsVO;


import java.util.List;

/**
 * 参数配置 服务类
 *
 */
public interface SysParamsService extends SuperService<SysParams> {

    /**
     * 获取参数配置列表
     *
     * @param sysParamsVO
     * @return
     */
    IPage<SysParams> getPageList(SysParamsVO sysParamsVO);

    /**
     * 通过 参数键名 获取参数配置
     *
     * @param paramsKey
     * @return
     */
    SysParams getSysParamsByKey(String paramsKey);

    /**
     * 通过 参数键名 获取参数值
     *
     * @param paramsKey
     * @return
     */
    String getSysParamsValueByKey(String paramsKey);

    /**
     * 新增参数配置
     *
     * @param sysParamsVO
     */
    String addSysParams(SysParamsVO sysParamsVO);

    /**
     * 编辑参数配置
     *
     * @param sysParamsVO
     */
    String editSysParams(SysParamsVO sysParamsVO);

    /**
     * 批量删除参数配置
     *
     * @param sysParamsVOList
     */
    String deleteBatchSysParams(List<SysParamsVO> sysParamsVOList);
}
