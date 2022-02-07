package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
