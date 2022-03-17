package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.xo.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:24
 */
@RestController
@RequestMapping("/sort")
@Slf4j
public class SortRestApi {

    @Autowired
    BlogService blogService;

    /**
     * 获取归档的信息
     */
    @GetMapping("/getSortList")
    public String getSortList() {
        log.info("获取归档日期");
        return blogService.getBlogTimeSortList();
    }

    @GetMapping("/getArticleByMonth")
    public String getArticleByMonth(@RequestParam(name = "monthDate", required = false) String monthDate) {
        log.info("通过月份获取文章列表");
        return blogService.getArticleByMonth(monthDate);
    }
}
