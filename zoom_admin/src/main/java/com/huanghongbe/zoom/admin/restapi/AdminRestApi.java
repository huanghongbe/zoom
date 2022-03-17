package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.validator.group.Delete;
import com.huanghongbe.zoom.base.validator.group.Insert;
import com.huanghongbe.zoom.base.validator.group.Update;
import com.huanghongbe.zoom.base.validator.group.GetList;
import com.huanghongbe.zoom.xo.service.AdminService;
import com.huanghongbe.zoom.xo.vo.AdminVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员表 RestApi
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminRestApi {

    @Autowired
    private AdminService adminService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.getList(adminVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "重置用户密码")
    @PostMapping("/restPwd")
    public String restPwd(@Validated({Update.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.resetPwd(adminVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "新增管理员")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.addAdmin(adminVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "更新管理员")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.editAdmin(adminVO);
    }


    @AuthorityVerify
    @OperationLogger(value = "批量删除管理员")
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class})@RequestParam(name = "adminUids") List<String> adminUids) {
        return adminService.deleteBatchAdmin(adminUids);
    }

    @AuthorityVerify
    @PostMapping(value = "/getOnlineAdminList")
    public String getOnlineAdminList(@Validated({GetList.class}) @RequestBody AdminVO adminVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return adminService.getOnlineAdminList(adminVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "强退用户")
    @PostMapping(value = "/forceLogout")
    public String forceLogout(@RequestBody List<String> tokenUidList) {
        return adminService.forceLogout(tokenUidList);
    }
}

