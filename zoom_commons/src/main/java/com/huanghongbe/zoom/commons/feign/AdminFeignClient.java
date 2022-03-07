package com.huanghongbe.zoom.commons.feign;

import com.huanghongbe.zoom.commons.config.feign.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-03-07 9:32
 */
@FeignClient(name = "zoom-admin", configuration = FeignConfiguration.class)
public interface AdminFeignClient {
    /**
     * 获取系统配置信息
     */
    @RequestMapping(value = "/systemConfig/getSystemConfig", method = RequestMethod.GET)
    String getSystemConfig();
}
