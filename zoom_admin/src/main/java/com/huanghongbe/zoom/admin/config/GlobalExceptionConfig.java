package com.huanghongbe.zoom.admin.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * 全局异常处理配置
 *
 */
@Configuration
public class GlobalExceptionConfig {

    @Bean
    public HandlerExceptionResolver getHandlerExceptionResolver() {
        return new com.huanghongbe.zoom.base.handler.HandlerExceptionResolver();
    }
}
