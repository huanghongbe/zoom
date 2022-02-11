package com.huanghongbe.zoom.xo.vo;

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
    private String text;
    /**
     * 表示事项是否完成
     */
    private Boolean done;



}
