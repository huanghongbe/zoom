package com.huanghongbe.zoom.xo.vo;

import com.huanghongbe.zoom.base.vo.BaseVO;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 图片实体类
 *
 */
@ToString
@Data
public class PictureVO extends BaseVO<PictureVO> {

    /**
     * 图片UID
     */
    private String fileUid;

    /**
     * 图片UIDs
     */
    private String fileUids;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 所属相册分类UID
     */
    private String pictureSortUid;
}
