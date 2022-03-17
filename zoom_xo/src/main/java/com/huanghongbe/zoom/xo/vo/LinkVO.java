package com.huanghongbe.zoom.xo.vo;

import com.huanghongbe.zoom.base.validator.annotion.NotBlank;
import com.huanghongbe.zoom.base.validator.group.Insert;
import com.huanghongbe.zoom.base.validator.group.Update;
import com.huanghongbe.zoom.base.vo.BaseVO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-27 16:22
 */
@Data
@NoArgsConstructor
public class LinkVO extends BaseVO<LinkVO> {
    /**
     * 友链标题
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String title;
    /**
     * 友链介绍
     */
    private String summary;
    /**
     * 友链地址
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String url;

    /**
     * 友链状态： 0 申请中， 1：已上线，  2：已拒绝
     */
    private Integer linkStatus;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 站长邮箱
     */
    private String email;

    /**
     * 网站图标uid
     */
    private String fileUid;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

}
