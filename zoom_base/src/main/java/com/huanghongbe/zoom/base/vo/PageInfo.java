package com.huanghongbe.zoom.base.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ：huanghongbe
 * @description：泛型分页，T为任意类型对象
 * @date ：2022-01-13 23:21
 */
@Data
public class PageInfo<T> {
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 当前页
     */
    @NotNull
    private Long currentPage;
    /**
     * 页大小
     */
    @NotNull
    private Long pageSize;
}
