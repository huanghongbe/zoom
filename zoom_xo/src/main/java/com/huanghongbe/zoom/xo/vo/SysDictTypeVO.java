package com.huanghongbe.zoom.xo.vo;

import com.huanghongbe.zoom.base.validator.annotion.IntegerNotNull;
import com.huanghongbe.zoom.base.validator.annotion.NotBlank;
import com.huanghongbe.zoom.base.validator.group.Insert;
import com.huanghongbe.zoom.base.validator.group.Update;
import com.huanghongbe.zoom.base.vo.BaseVO;
import lombok.Data;

/**
 * <p>
 * SysDictTypeVO
 * </p>
 *
 */
@Data
public class SysDictTypeVO extends BaseVO<SysDictTypeVO> {


    /**
     * 自增键 oid
     */
    private Long oid;

    /**
     * 字典名称
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String dictName;

    /**
     * 字典类型
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String dictType;

    /**
     * 是否发布  1：是，0:否，默认为0
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String isPublish;

    /**
     * 备注
     */
    private String remark;

    /**
     * 排序字段
     */
    @IntegerNotNull(groups = {Insert.class, Update.class})
    private Integer sort;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

}
