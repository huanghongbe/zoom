package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.SysLog;
import com.huanghongbe.zoom.xo.vo.SysLogVO;

/**
 * 操作日志 服务类
 *
 */
public interface SysLogService extends SuperService<SysLog> {

    /**
     * 获取操作日志列表
     *
     * @param sysLogVO
     * @return
     */
    IPage<SysLog> getPageList(SysLogVO sysLogVO);
}
