package com.huanghongbe.zoom.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.huanghongbe.zoom.base.entity.SuperEntity;
import lombok.Data;

/**
 * <p>
 * 代办事项表
 * </p>
 *
 */
@Data
@TableName("t_todo")
public class Todo extends SuperEntity<Todo> {

    private static final long serialVersionUID = 1L;

    /**
     * 内容
     */
    private String text;

    /**
     * 管理员UID
     */
    private String adminUid;

    /**
     * 表示事项是否完成
     */
    private Boolean done;
}
