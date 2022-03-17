package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.validator.group.Update;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.AdminService;
import com.huanghongbe.zoom.xo.vo.AdminVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统设置RestApi
 *
 */

@RestController
@RequestMapping("/system")
@Slf4j
public class SystemRestApi {

    @Autowired
    private AdminService adminService;

    @AuthorityVerify
    @GetMapping("/getMe")
    public String getMe() {
        return ResultUtil.successWithData(adminService.getMe());
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑我的信息")
    @PostMapping("/editMe")
    public String editMe(@Validated({Update.class})@RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.editMe(adminVO);
    }

    @AuthorityVerify
    @PostMapping("/changePwd")
    public String changePwd(@RequestParam(name = "oldPwd", required = false) String oldPwd,
                            @RequestParam(name = "newPwd", required = false) String newPwd) {
        return adminService.changePwd(oldPwd, newPwd);
    }

}
