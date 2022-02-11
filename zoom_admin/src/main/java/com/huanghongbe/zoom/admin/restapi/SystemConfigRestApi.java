package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-12 1:42
 */
@RestController
@RequestMapping("/systemConfig")
@Slf4j
public class SystemConfigRestApi {
    @Autowired
    private SystemConfigService systemConfigService;

    @AuthorityVerify
    @GetMapping("/getSystemConfig")
    public String getSystemConfig() {
        return ResultUtil.successWithData(systemConfigService.getConfig());
    }
}
