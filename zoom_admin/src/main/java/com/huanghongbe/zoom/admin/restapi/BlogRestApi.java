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
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.vo.BlogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 博客表 RestApi
 *
 */

@RestController
@RequestMapping("/blog")
@Slf4j
public class BlogRestApi {

    @Autowired
    private BlogService blogService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(blogService.getPageList(blogVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加博客")
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return blogService.addBlog(blogVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "本地博客上传")
    @PostMapping("/uploadLocalBlog")
    public String uploadPics(@RequestBody List<MultipartFile> filedatas) throws IOException {

        return blogService.uploadLocalBlog(filedatas);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑博客")
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return blogService.editBlog(blogVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "推荐博客排序调整")
    @PostMapping("/editBatch")
    public String editBatch(@RequestBody List<BlogVO> blogVOList) {
        return blogService.editBatch(blogVOList);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除博客")
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return blogService.deleteBlog(blogVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除选中博客")
    @PostMapping("/deleteBatch")
    public String deleteBatch(@RequestBody List<BlogVO> blogVoList) {
        return blogService.deleteBatchBlog(blogVoList);
    }

}