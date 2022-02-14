package com.huanghongbe.zoom.admin.restapi;
import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.vo.BlogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
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
    public String getList(@RequestBody BlogVO blogVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(blogService.getPageList(blogVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @PostMapping("/add")
    public String add(@RequestBody BlogVO blogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return blogService.addBlog(blogVO);
    }

    @AuthorityVerify
    @PostMapping("/uploadLocalBlog")
    public String uploadPics(@RequestBody List<MultipartFile> filedatas) throws IOException {

        return blogService.uploadLocalBlog(filedatas);
    }

    @AuthorityVerify
    @PostMapping("/edit")
    public String edit(@RequestBody BlogVO blogVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return blogService.editBlog(blogVO);
    }

    @AuthorityVerify
    @PostMapping("/editBatch")
    public String editBatch(@RequestBody List<BlogVO> blogVOList) {
        return blogService.editBatch(blogVOList);
    }

    @AuthorityVerify
    @PostMapping("/delete")
    public String delete(@RequestBody BlogVO blogVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return blogService.deleteBlog(blogVO);
    }

    @AuthorityVerify
    @PostMapping("/deleteBatch")
    public String deleteBatch(@RequestBody List<BlogVO> blogVoList) {
        return blogService.deleteBatchBlog(blogVoList);
    }

}