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
@TableName("t_blog_sort")
public class BlogSort extends SuperEntity<BlogSort> {

    private static final long serialVersionUID = 4810365823140534070L;
}
