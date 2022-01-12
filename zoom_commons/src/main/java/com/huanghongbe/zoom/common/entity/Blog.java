package com.huanghongbe.zoom.common.entity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huanghongbe.zoom.base.entity.SuperEntity;
import lombok.Data;

/**
 * 博客表
 * @TableName t_blog
 */
@Data
@TableName("t_blog")
public class Blog extends SuperEntity<Blog> {
    /**
     * 唯一uid
     */
    private String uid;

    /**
     * 唯一oid
     */
    private Integer oid;

    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客简介
     */
    private String summary;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 标签uid
     */
    private String tagUid;

    /**
     * 博客点击数
     */
    private Integer clickCount;

    /**
     * 博客收藏数
     */
    private Integer collectCount;

    /**
     * 标题图片uid
     */
    private String fileUid;

    /**
     * 状态
     */
    private Byte status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 管理员uid
     */
    private String adminUid;

    /**
     * 是否原创（0:不是 1：是）
     */
    private String isOriginal;

    /**
     * 作者
     */
    private String author;

    /**
     * 文章出处
     */
    private String articlesPart;

    /**
     * 博客分类UID
     */
    private String blogSortUid;

    /**
     * 推荐等级(0:正常)
     */
    private Boolean level;

    /**
     * 是否发布：0：否，1：是
     */
    private String isPublish;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 是否开启评论(0:否 1:是)
     */
    private Boolean openComment;

    /**
     * 类型【0 博客， 1：推广】
     */
    private Boolean type;

    /**
     * 外链【如果是推广，那么将跳转到外链】
     */
    private String outsideLink;

    private static final long serialVersionUID = 1L;
}