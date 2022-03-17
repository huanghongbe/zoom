package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.service.SystemConfigService;
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
 * @date ：2022-02-07 17:07
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchRestApi {
    @Autowired
    private BlogService blogService;
    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping(value = "/getSearchModel")
    public String getSearchModel() {
        return ResultUtil.successWithData(systemConfigService.getSearchModel());
    }

    @GetMapping("/sqlSearchBlog")
    public String sqlSearchBlog(@RequestParam(required = true) String keywords,
                                @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {

        if (StringUtils.isEmpty(keywords) || StringUtils.isEmpty(keywords.trim())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogByKeyword(keywords, currentPage, pageSize));

    }
    @GetMapping("/searchBlogByTag")
    public String searchBlogByTag(HttpServletRequest request,
                                  @RequestParam(name = "tagUid", required = true) String tagUid,
                                  @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtil.result(SysConf.ERROR, "标签不能为空");
        }
        return ResultUtil.result(SysConf.SUCCESS, blogService.searchBlogByTag(tagUid, currentPage, pageSize));
    }
    @GetMapping("/searchBlogBySort")
    public String searchBlogBySort(HttpServletRequest request,
                                   @RequestParam(name = "blogSortUid", required = true) String blogSortUid,
                                   @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                   @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(blogSortUid)) {
            return ResultUtil.result(SysConf.ERROR, "uid不能为空");
        }
        return ResultUtil.result(SysConf.SUCCESS, blogService.searchBlogByBlogSort(blogSortUid, currentPage, pageSize));
    }
    @GetMapping("/searchBlogByAuthor")
    public String searchBlogByAuthor(HttpServletRequest request,
                                     @RequestParam(name = "author", required = true) String author,
                                     @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                     @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(author)) {
            return ResultUtil.result(SysConf.ERROR, "作者不能为空");
        }
        return ResultUtil.result(SysConf.SUCCESS, blogService.searchBlogByAuthor(author, currentPage, pageSize));
    }
}
