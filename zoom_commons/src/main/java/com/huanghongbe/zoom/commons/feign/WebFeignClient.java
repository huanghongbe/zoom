package com.huanghongbe.zoom.commons.feign;

import com.huanghongbe.zoom.commons.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 后端服务feign远程调用
 *
 */

@FeignClient(name = "zoom-web", configuration = FeignConfiguration.class)
public interface WebFeignClient {

    /**
     * 获取系统配置信息
     */
    @RequestMapping(value = "/oauth/getSystemConfig", method = RequestMethod.GET)
    String getSystemConfig(@RequestParam("token") String token);

    /**
     * 获取搜索模式
     * @return
     */
    @RequestMapping(value = "/search/getSearchModel", method = RequestMethod.GET)
    String getSearchModel();

    @RequestMapping("/content/getBlogByUid")
    String getBlogByUid(@RequestParam(name = "uid", required = false) String uid);

    @RequestMapping("/content/getSameBlogByTagUid")
    String getSameBlogByTagUid(@RequestParam(name = "tagUid", required = true) String tagUid,
                                      @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize);

    @RequestMapping("/content/getSameBlogByBlogUid")
    String getSameBlogByBlogUid(@RequestParam(name = "blogUid", required = true) String blogUid, Long currentPage, @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize);

    /**
     * 获取博客列表[包含内容]
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/index/getBlogBySearch")
    String getBlogBySearch(@RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize);

}