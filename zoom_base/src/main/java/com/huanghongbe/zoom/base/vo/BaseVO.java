package com.huanghongbe.zoom.base.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:21
 */
@Data
public class BaseVO<T> extends PageInfo<T>{
    @TableId(type = IdType.UUID)
    private String uid;
    private Integer status;
}
