package com.huanghongbe.zoom.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huanghongbe.zoom.base.entity.SuperEntity;
import lombok.Data;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:30
 */
@Data
@TableName("t_blog_sort")
public class BlogSort extends SuperEntity<BlogSort> {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名
     */
    private String sortName;

    /**
     * 分类介绍
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String content;

    /**
     * 点击数
     */
    private Integer clickCount;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;
}
