package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.ExceptionLog;
import com.huanghongbe.zoom.xo.vo.ExceptionLogVO;

/**
 * 操作异常日志 服务类
 *
 */
public interface ExceptionLogService extends SuperService<ExceptionLog> {

    /**
     * 获取异常日志列表
     *
     * @param exceptionLogVO
     * @return
     */
    public IPage<ExceptionLog> getPageList(ExceptionLogVO exceptionLogVO);
}
