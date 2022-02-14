package com.huanghongbe.zoom.admin.restapi;
import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.BlogSortService;
import com.huanghongbe.zoom.xo.vo.BlogSortVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 博客分类表 RestApi
 *
 */
@RestController
@RequestMapping("/blogSort")
@Slf4j
public class BlogSortRestApi {

    @Autowired
    private BlogSortService blogSortService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取博客分类列表");
        return ResultUtil.successWithData(blogSortService.getPageList(blogSortVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @PostMapping("/add")
    public String add(@RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加博客分类");
        return blogSortService.addBlogSort(blogSortVO);
    }

    @AuthorityVerify
    @PostMapping("/edit")
    public String edit(@RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑博客分类");
        return blogSortService.editBlogSort(blogSortVO);
    }

    @AuthorityVerify
    @PostMapping("/deleteBatch")
    public String delete(@RequestBody List<BlogSortVO> blogSortVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除博客分类");
        return blogSortService.deleteBatchBlogSort(blogSortVoList);
    }

    @AuthorityVerify
    @PostMapping("/stick")
    public String stick(@RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("置顶分类");
        return blogSortService.stickBlogSort(blogSortVO);

    }

    @AuthorityVerify
    @PostMapping("/blogSortByClickCount")
    public String blogSortByClickCount() {
        log.info("通过点击量排序博客分类");
        return blogSortService.blogSortByClickCount();
    }

    /**
     * 通过引用量排序标签
     * 引用量就是所有的文章中，有多少使用了该标签，如果使用的越多，该标签的引用量越大，那么排名越靠前
     *
     * @return
     */
    @AuthorityVerify
    @PostMapping("/blogSortByCite")
    public String blogSortByCite() {
        log.info("通过引用量排序博客分类");
        return blogSortService.blogSortByCite();
    }
}

