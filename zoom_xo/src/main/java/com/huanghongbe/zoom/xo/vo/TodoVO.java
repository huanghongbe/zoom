package com.huanghongbe.zoom.xo.vo;

import com.huanghongbe.zoom.base.validator.annotion.BooleanNotNULL;
import com.huanghongbe.zoom.base.validator.annotion.NotBlank;
import com.huanghongbe.zoom.base.validator.group.GetOne;
import com.huanghongbe.zoom.base.validator.group.Insert;
import com.huanghongbe.zoom.base.validator.group.Update;
import com.huanghongbe.zoom.base.vo.BaseVO;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TodoVO
 *
 */
@Data
@NoArgsConstructor
public class TodoVO extends BaseVO<TodoVO> {

    /**
     * 内容
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String text;
    /**
     * 表示事项是否完成
     */
    @BooleanNotNULL(groups = {Update.class, GetOne.class})
    private Boolean done;



}
