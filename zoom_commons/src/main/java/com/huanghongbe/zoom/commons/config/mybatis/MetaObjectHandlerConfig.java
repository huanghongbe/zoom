package com.huanghongbe.zoom.commons.config.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 用于自定义数据填充逻辑
 * insert和update填充时间
 */
@Slf4j
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("插入方法填充");
        Date date=new Date();
        setFieldValByName("createTime", date, metaObject);
        setFieldValByName("updateTime", date, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("更新方法填充");
        setFieldValByName("updateTime", new Date(), metaObject);
    }
}
