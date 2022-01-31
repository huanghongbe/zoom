package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.commons.entity.Link;
import com.huanghongbe.zoom.commons.entity.Tag;
import com.huanghongbe.zoom.utils.JsonUtils;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-01 4:11
 */
@RestController
@RequestMapping("/index")
@Slf4j
public class IndexRestApi {

    @Autowired
    private TagService tagService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private WebNavbarService webNavbarService;
    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/getBlogByLevel")
    public String getBlogByLevel(HttpServletRequest request,
                                 @RequestParam(name = "level", required = false, defaultValue = "0") Integer level,
                                 @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                 @RequestParam(name = "useSort", required = false, defaultValue = "0") Integer useSort) {

        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogPageByLevel(level, currentPage, useSort));
    }

    @GetMapping("/getHotBlog")
    public String getHotBlog() {
        log.info("获取首页排行博客");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getHotBlog());
    }

    @GetMapping("/getNewBlog")
    public String getNewBlog(HttpServletRequest request,
                             @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                             @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        log.info("获取首页最新的博客");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getNewBlog(currentPage, null));
    }

    @GetMapping("/getBlogBySearch")
    public String getBlogBySearch(HttpServletRequest request,
                                  @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        log.info("获取首页最新的博客");
        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogBySearch(currentPage, null));
    }


    @GetMapping("/getBlogByTime")
    public String getBlogByTime(HttpServletRequest request,
                                @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
        return ResultUtil.result(SysConf.SUCCESS, blogService.getBlogByTime(currentPage, Long.valueOf(blogNewCount)));
    }

    @GetMapping("/getHotTag")
    public String getHotTag() {
        String hotTagCount = sysParamsService.getSysParamsValueByKey(SysConf.HOT_TAG_COUNT);
        // 从Redis中获取友情链接
        String jsonResult = redisUtil.get(RedisConf.BLOG_TAG + Constants.SYMBOL_COLON + hotTagCount);
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            return ResultUtil.result(SysConf.SUCCESS, jsonResult2List);
        }
        List<Tag> tagList = tagService.getHotTag(Integer.valueOf(hotTagCount));
        if (tagList.size() > 0) {
            redisUtil.setEx(RedisConf.BLOG_TAG + Constants.SYMBOL_COLON + hotTagCount, JsonUtils.objectToJson(tagList), 1, TimeUnit.HOURS);
        }
        return ResultUtil.result(SysConf.SUCCESS, tagList);
    }

    @GetMapping("/getLink")
    public String getLink() {
        String friendlyLinkCount = sysParamsService.getSysParamsValueByKey(SysConf.FRIENDLY_LINK_COUNT);
        // 从Redis中获取友情链接
        String jsonResult = redisUtil.get(RedisConf.BLOG_LINK + Constants.SYMBOL_COLON + friendlyLinkCount);
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            return ResultUtil.result(SysConf.SUCCESS, jsonResult2List);
        }
        List<Link> linkList = linkService.getListByPageSize(Integer.valueOf(friendlyLinkCount));
        if (linkList.size() > 0) {
            redisUtil.setEx(RedisConf.BLOG_LINK + Constants.SYMBOL_COLON + friendlyLinkCount, JsonUtils.objectToJson(linkList), 1, TimeUnit.HOURS);
        }
        return ResultUtil.result(SysConf.SUCCESS, linkList);
    }

    @GetMapping("/addLinkCount")
    public String addLinkCount(@RequestParam(name = "uid", required = false) String uid) {
        log.info("点击友链");
        return linkService.addLinkCount(uid);
    }

    @GetMapping("/getWebConfig")
    public String getWebConfig() {
        log.info("获取网站配置");
        return ResultUtil.result(SysConf.SUCCESS, webConfigService.getWebConfigByShowList());
    }

    @GetMapping("/getWebNavbar")
    public String getWebNavbar() {
        log.info("获取网站导航栏");
        return ResultUtil.result(SysConf.SUCCESS, webNavbarService.getAllList());
    }

    @GetMapping("/recorderVisitPage")
    public String recorderVisitPage(@RequestParam(name = "pageName", required = true) String pageName) {
        if (StringUtils.isEmpty(pageName)) {
            return ResultUtil.result(SysConf.SUCCESS, MessageConf.PARAM_INCORRECT);
        }
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }
}
