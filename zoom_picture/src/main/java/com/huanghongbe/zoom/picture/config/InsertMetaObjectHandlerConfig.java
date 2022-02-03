package com.huanghongbe.zoom.picture.config;

import com.huanghongbe.zoom.commons.config.mybatis.MetaObjectHandlerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-04 4:26
 */
@Component
public class InsertMetaObjectHandlerConfig {
    @Bean
    public MetaObjectHandlerConfig insertMetaObjectHandlerConfig(){
        return new MetaObjectHandlerConfig();
    }
}
