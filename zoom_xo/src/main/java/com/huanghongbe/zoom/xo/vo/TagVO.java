package com.huanghongbe.zoom.xo.vo;


import com.huanghongbe.zoom.base.vo.BaseVO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BlogVO
 *
 */
@Data
@NoArgsConstructor
public class TagVO extends BaseVO<TagVO> {

    /**
     * 标签内容
     */
    private String content;

    /**
     * 排序字段
     */
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
