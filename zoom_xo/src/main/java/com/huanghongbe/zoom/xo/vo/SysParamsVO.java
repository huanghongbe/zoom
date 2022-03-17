package com.huanghongbe.zoom.xo.vo;

import com.huanghongbe.zoom.base.validator.annotion.IntegerNotNull;
import com.huanghongbe.zoom.base.validator.annotion.NotBlank;
import com.huanghongbe.zoom.base.validator.group.Insert;
import com.huanghongbe.zoom.base.validator.group.Update;
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
    @NotBlank(groups = {Insert.class, Update.class})
    private String paramsName;

    /**
     * 参数键名
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String paramsKey;

    /**
     * 参数键值
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String paramsValue;

    /**
     * 参数类型，是否系统内置（1：是，0：否）
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer paramsType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer sort;

}
