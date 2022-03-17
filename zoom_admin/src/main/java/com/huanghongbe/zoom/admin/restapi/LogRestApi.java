package com.huanghongbe.zoom.admin.restapi;


import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.enums.SysConf;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.validator.group.GetList;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.ExceptionLogService;
import com.huanghongbe.zoom.xo.service.SysLogService;
import com.huanghongbe.zoom.xo.vo.ExceptionLogVO;
import com.huanghongbe.zoom.xo.vo.SysLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志记录表 RestApi
 *
 */
@RestController
@RequestMapping("/log")
@Slf4j
public class LogRestApi {

    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private ExceptionLogService exceptionLogService;

    @AuthorityVerify
    @PostMapping(value = "/getLogList")
    public String getLogList(@Validated({GetList.class})@RequestBody SysLogVO sysLogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, sysLogService.getPageList(sysLogVO));
    }

    @AuthorityVerify
    @PostMapping(value = "/getExceptionList")
    public String getExceptionList(@Validated({GetList.class}) @RequestBody ExceptionLogVO exceptionLogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, exceptionLogService.getPageList(exceptionLogVO));
    }
}

