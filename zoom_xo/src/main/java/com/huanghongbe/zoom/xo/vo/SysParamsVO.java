package com.huanghongbe.zoom.xo.vo;

import com.huanghongbe.zoom.base.vo.BaseVO;
import lombok.Data;

/**
 * TodoVO
 *
 */
@Data
public class SysParamsVO extends BaseVO<SysParamsVO> {


    /**
     * 参数名称
     */
    private String paramsName;

    /**
     * 参数键名
     */
    private String paramsKey;

    /**
     * 参数键值
     */
    private String paramsValue;

    /**
     * 参数类型，是否系统内置（1：是，0：否）
     */
    private Integer paramsType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    private Integer sort;

}
