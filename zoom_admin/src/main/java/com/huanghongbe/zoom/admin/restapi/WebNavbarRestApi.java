package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.WebNavbarService;
import com.huanghongbe.zoom.xo.vo.WebNavbarVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 门户导航栏管理
 *
 */
@RestController
@RequestMapping("/webNavbar")
@Slf4j
public class WebNavbarRestApi {

    @Autowired
    private WebNavbarService webNavbarService;

    @AuthorityVerify
    @GetMapping("/getList")
    public String getList(@RequestBody WebNavbarVO webNavbarVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(webNavbarService.getPageList(webNavbarVO));
    }

    @GetMapping("/getAllList")
    public String getAllList() {
        return ResultUtil.successWithData(webNavbarService.getAllList());
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加门户导航栏")
    @PostMapping("/add")
    public String add(@RequestBody WebNavbarVO webNavbarVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加门户导航栏");
        return webNavbarService.addWebNavbar(webNavbarVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑门户导航栏")
    @PostMapping("/edit")
    public String edit(@RequestBody WebNavbarVO webNavbarVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑门户导航栏");
        return webNavbarService.editWebNavbar(webNavbarVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除门户导航栏")
    @PostMapping("/delete")
    public String delete(@RequestBody WebNavbarVO webNavbarVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除门户导航栏");
        return webNavbarService.deleteWebNavbar(webNavbarVO);
    }
}

