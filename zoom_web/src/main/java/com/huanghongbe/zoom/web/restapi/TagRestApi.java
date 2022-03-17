package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.BlogService;
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
 * @date ：2022-01-30 4:26
 */
@RestController
@RequestMapping("/tag")
@Slf4j
public class TagRestApi {

    @Autowired
    private BlogService blogService;
    @Autowired
    private TagService tagService;

    /**
     * 获取标签的信息
     *
     * @return
     */
    @GetMapping("/getTagList")
    public String getTagList() {
        log.info("获取标签信息");
        return ResultUtil.result(SysConf.SUCCESS, tagService.getList());
    }

    /**
     * 通过TagUid获取文章
     *
     * @param request
     * @param currentPage
     * @param pageSize
     * @return
     */
    @GetMapping("/getArticleByTagUid")
    public String getArticleByTagUid(HttpServletRequest request,
                                     @RequestParam(name = "tagUid", required = false) String tagUid,
                                     @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                     @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtil.result(SysConf.ERROR, "传入TagUid不能为空");
        }
        log.info("通过blogSortUid获取文章列表");
        return ResultUtil.result(SysConf.SUCCESS, blogService.searchBlogByTag(tagUid, currentPage, pageSize));
    }

}
