package com.huanghongbe.zoom.commons.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huanghongbe.zoom.base.entity.SuperEntity;
import lombok.Data;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 3:55
 */
@Data
@TableName("t_subject_item")
public class SubjectItem extends SuperEntity<SubjectItem> {
    private static final long serialVersionUID = 1L;

    /**
     * 专题UID
     */
    private String subjectUid;
    /**
     * 博客uid
     */
    private String blogUid;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;

    /**
     * 博客
     */
    @TableField(exist = false)
    private Blog blog;
}
