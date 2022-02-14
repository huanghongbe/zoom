package com.huanghongbe.zoom.picture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;


@EnableTransactionManagement
@SpringBootApplication
@EnableOpenApi
@EnableDiscoveryClient
@EnableFeignClients("com.huanghongbe.zoom.commons.feign")
@ComponentScan(basePackages = {
        "com.huanghongbe.zoom.commons.config.feign",
//        "com.huanghongbe.zoom.commons.handler",
        "com.huanghongbe.zoom.commons.config.redis",
        "com.huanghongbe.zoom.utils",
        "com.huanghongbe.zoom.picture"})
//@EnableDubbo
public class PictureApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureApplication.class, args);
    }
}
