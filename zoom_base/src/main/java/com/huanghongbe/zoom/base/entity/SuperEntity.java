package com.huanghongbe.zoom.base.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.huanghongbe.zoom.base.enums.EStatus;
import lombok.Data;


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
    private String uid;

    /**
     * 状态 0：失效 1：生效
     */
    private int status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public SuperEntity(){
        status= EStatus.ENABLED;
        Date date=new Date();
        createTime=date;
        updateTime=date;
    }
}
