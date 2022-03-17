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
import com.huanghongbe.zoom.xo.service.SubjectService;
import com.huanghongbe.zoom.xo.vo.SubjectVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 专题表 RestApi
 *
 */
@RestController
@RequestMapping("/subject")
@Slf4j
public class SubjectRestApi {

    @Autowired
    private SubjectService subjectService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class})@RequestBody SubjectVO subjectVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(subjectService.getPageList(subjectVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加专题")
    @PostMapping("/add")
    public String add(@Validated({Insert.class})@RequestBody SubjectVO subjectVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectService.addSubject(subjectVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑专题")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class})@RequestBody SubjectVO subjectVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectService.editSubject(subjectVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除专题")
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class})@RequestBody List<SubjectVO> subjectVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectService.deleteBatchSubject(subjectVOList);
    }
}

