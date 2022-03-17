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
import com.huanghongbe.zoom.xo.service.CommentService;
import com.huanghongbe.zoom.xo.vo.CommentVO;
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
 * 评论表 RestApi
 *
 */
@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentRestApi {

    @Autowired
    CommentService commentService;

    @AuthorityVerify
    @PostMapping(value = "/getList")
    public String getList(@Validated({GetList.class}) @RequestBody CommentVO commentVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取评论列表: {}", commentVO);
        return ResultUtil.successWithData(commentService.getPageList(commentVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody CommentVO commentVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("新增评论: {}", commentVO);
        return commentService.addComment(commentVO);
    }

    @AuthorityVerify
    @PostMapping("/edit")
    public String edit(@Validated({Update.class})@RequestBody CommentVO commentVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑评论: {}", commentVO);
        return commentService.editComment(commentVO);
    }

    @AuthorityVerify
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class})@RequestBody CommentVO commentVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("删除评论: {}", commentVO);
        return commentService.deleteComment(commentVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除选中评论")
    @PostMapping("/deleteBatch")
    public String deleteBatch(@Validated({Delete.class})@RequestBody List<CommentVO> commentVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return commentService.deleteBatchComment(commentVoList);
    }


}

