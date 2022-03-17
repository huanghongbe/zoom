package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.validator.group.Delete;
import com.huanghongbe.zoom.base.validator.group.GetList;
import com.huanghongbe.zoom.base.validator.group.Insert;
import com.huanghongbe.zoom.base.validator.group.Update;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.UserService;
import com.huanghongbe.zoom.xo.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户表 RestApi
 *
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserRestApi {

    @Autowired
    private UserService userService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class})@RequestBody UserVO userVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取用户列表: {}", userVO);
        return ResultUtil.successWithData(userService.getPageList(userVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "新增用户")
    @PostMapping("/add")
    public String add(@Validated({Insert.class})@RequestBody UserVO userVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("新增用户: {}", userVO);
        return userService.addUser(userVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑用户")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class})@RequestBody UserVO userVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑用户: {}", userVO);
        return userService.editUser(userVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除用户")
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class})@RequestBody UserVO userVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除用户: {}", userVO);
        return userService.deleteUser(userVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "重置用户密码")
    @PostMapping("/resetUserPassword")
    public String resetUserPassword(@RequestBody UserVO userVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("重置用户密码: {}", userVO);
        return userService.resetUserPassword(userVO);
    }
}