package com.huanghongbe.zoom.xo.vo;


import com.huanghongbe.zoom.base.vo.BaseVO;
import lombok.Data;

/**
 * RoleVO
 *
 */
@Data
public class RoleVO extends BaseVO<RoleVO> {


    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 介绍
     */
    private String summary;

    /**
     * 该角色所能管辖的区域
     */
    private String categoryMenuUids;

}
