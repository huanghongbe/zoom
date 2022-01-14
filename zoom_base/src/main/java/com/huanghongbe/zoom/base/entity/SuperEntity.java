package com.huanghongbe.zoom.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.huanghongbe.zoom.base.enums.EStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-12 0:38
 */
@Data
public class SuperEntity<T extends Model> extends Model{
    private static final long serialVersionUID = 1310878133376149230L;

    /**
     * 唯一UID
     */
    @TableId(value = "uid", type = IdType.UUID)
    private String uid;

    /**
     * 状态 0：失效 1：生效
     */
    private int status;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date createTime;

    /**
     * 更新时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    public SuperEntity(){
        status= EStatus.ENABLE;
        Date date=new Date();
        createTime=date;
        updateTime=date;
    }
}
