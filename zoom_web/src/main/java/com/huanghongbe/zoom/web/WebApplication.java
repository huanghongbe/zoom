package com.huanghongbe.zoom.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:53
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.huanghongbe.zoom.commons.config",
//        "com.huanghongbe.zoom.commons.fallback",
        "com.huanghongbe.zoom.utils",
        "com.huanghongbe.zoom.xo.utils",
        "com.huanghongbe.zoom.web",
        "com.huanghongbe.zoom.xo.service"})
public class WebApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(WebApplication.class,args);
    }
    /**
     * 设置时区
     */
    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
