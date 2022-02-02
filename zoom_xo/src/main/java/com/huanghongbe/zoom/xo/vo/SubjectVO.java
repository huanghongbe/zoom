package com.huanghongbe.zoom.xo.vo;

import com.huanghongbe.zoom.base.vo.BaseVO;
import lombok.Data;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:48
 */
@Data
public class SubjectVO extends BaseVO<SubjectVO>{
    /**
     * 专题名
     */
    private String subjectName;

    /**
     * 专题介绍
     */
    private String summary;

    /**
     * 封面图片UID
     */
    private String fileUid;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 专题点击数
     */
    private String clickCount;

    /**
     * 专题收藏数
     */
    private String collectCount;
}
