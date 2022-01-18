package com.huanghongbe.zoom.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.huanghongbe.zoom.base.entity.SuperEntity;
import lombok.Data;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:30
 */
@Data
@TableName("t_tag")
public class Tag extends SuperEntity<Tag> {

    private static final long serialVersionUID = 1L;

    /**
     * 标签内容
     */
    private String content;

    /**
     * 标签简介
     */
    private int clickCount;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;
}
