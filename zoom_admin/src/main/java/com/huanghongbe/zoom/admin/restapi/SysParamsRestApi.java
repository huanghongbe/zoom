package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.validator.group.Delete;
import com.huanghongbe.zoom.base.validator.group.GetList;
import com.huanghongbe.zoom.base.validator.group.Insert;
import com.huanghongbe.zoom.base.validator.group.Update;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.SysParamsService;
import com.huanghongbe.zoom.xo.vo.SysParamsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 参数配置 RestApi
 *
 */
@RestController
@RequestMapping("/sysParams")
@Slf4j
public class SysParamsRestApi {

    @Autowired
    private SysParamsService sysParamsService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class})@RequestBody SysParamsVO SysParamsVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取参数配置列表");
        return ResultUtil.successWithData(sysParamsService.getPageList(SysParamsVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加参数配置")
    @PostMapping("/add")
    public String add(@Validated({Insert.class})@RequestBody SysParamsVO sysParamsVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysParamsService.addSysParams(sysParamsVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑参数配置")
    @PostMapping("/edit")
    public String edit(HttpServletRequest request, @Validated({Update.class}) @RequestBody SysParamsVO SysParamsVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysParamsService.editSysParams(SysParamsVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除参数配置")
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class})@RequestBody List<SysParamsVO> SysParamsVoList, BindingResult result) {
        return sysParamsService.deleteBatchSysParams(SysParamsVoList);
    }
}

