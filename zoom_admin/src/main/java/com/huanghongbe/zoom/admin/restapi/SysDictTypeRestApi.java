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
import com.huanghongbe.zoom.xo.service.SysDictTypeService;
import com.huanghongbe.zoom.xo.vo.SysDictTypeVO;
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
 * 字典类型 RestApi
 *
 */
@RestController
@RequestMapping("/sysDictType")
@Slf4j
public class SysDictTypeRestApi {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class})@RequestBody SysDictTypeVO sysDictTypeVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取字典类型列表");
        return ResultUtil.successWithData(sysDictTypeService.getPageList(sysDictTypeVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加字典类型")
    @PostMapping("/add")
    public String add(@Validated({Insert.class})@RequestBody SysDictTypeVO sysDictTypeVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictTypeService.addSysDictType(sysDictTypeVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑字典类型")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class})HttpServletRequest request, @RequestBody SysDictTypeVO sysDictTypeVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictTypeService.editSysDictType(sysDictTypeVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除字典类型")
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class})HttpServletRequest request, @RequestBody List<SysDictTypeVO> sysDictTypeVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictTypeService.deleteBatchSysDictType(sysDictTypeVoList);
    }
}

