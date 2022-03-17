package com.huanghongbe.zoom.admin;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * zoom-admin 启动类
 *
 */
@EnableTransactionManagement
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableRabbit
@EnableFeignClients("com.huanghongbe.zoom.commons.feign")
@ComponentScan(basePackages = {
        "com.huanghongbe.zoom.commons.config",
        "com.huanghongbe.zoom.commons.fallback",
        "com.huanghongbe.zoom.utils",
        "com.huanghongbe.zoom.admin",
        "com.huanghongbe.zoom.xo.utils",
        "com.huanghongbe.zoom.xo.service"
})
public class AdminApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(AdminApplication.class, args);
    }

    /**
     * 设置时区
     */
    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
