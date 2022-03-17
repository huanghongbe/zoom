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
import com.huanghongbe.zoom.xo.service.SubjectItemService;
import com.huanghongbe.zoom.xo.vo.SubjectItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专题Item表 RestApi
 *
 */
@RestController
@RequestMapping("/subjectItem")
@Slf4j
public class SubjectItemRestApi {

    @Autowired
    private SubjectItemService subjectItemService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody SubjectItemVO subjectItemVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(subjectItemService.getPageList(subjectItemVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加专题Item")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody List<SubjectItemVO> subjectItemVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectItemService.addSubjectItemList(subjectItemVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑专题Item")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody List<SubjectItemVO> subjectItemVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectItemService.editSubjectItemList(subjectItemVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除专题Item")
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<SubjectItemVO> subjectItemVOList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return subjectItemService.deleteBatchSubjectItem(subjectItemVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "通过创建时间排序专题列表")
    @PostMapping("/sortByCreateTime")
    public String sortByCreateTime( @RequestParam(name = "subjectUid", required = true) String subjectUid,
                                    @RequestParam(name = "isDesc", required = false, defaultValue = "false") Boolean isDesc) {
        log.info("通过点击量排序博客分类");
        return subjectItemService.sortByCreateTime(subjectUid, isDesc);
    }
}

