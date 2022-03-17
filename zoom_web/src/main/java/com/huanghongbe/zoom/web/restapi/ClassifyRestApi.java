package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.service.BlogSortService;
import com.huanghongbe.zoom.xo.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-01 4:18
 */
@RestController
@RequestMapping("/classify")
@Slf4j
public class ClassifyRestApi {

    @Autowired
    BlogService blogService;
    @Autowired
    TagService tagService;
    @Autowired
    BlogSortService blogSortService;

    /**
     * 获取分类的信息
     */
    @GetMapping("/getBlogSortList")
    public String getBlogSortList() {
        log.info("获取分类信息");
        return ResultUtil.result(SysConf.SUCCESS, blogSortService.getList());
    }

    @GetMapping("/getArticleByBlogSortUid")
    public String getArticleByBlogSortUid(HttpServletRequest request,
                                          @RequestParam(name = "blogSortUid", required = false) String blogSortUid,
                                          @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                          @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(blogSortUid)) {
            log.info("点击分类,传入BlogSortUid不能为空");
            return ResultUtil.result(SysConf.ERROR, "传入BlogSortUid不能为空");
        }
        log.info("通过blogSortUid获取文章列表");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getListByBlogSortUid(blogSortUid, currentPage, pageSize));
    }
}
