package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.BlogSort;
import com.huanghongbe.zoom.xo.vo.BlogSortVO;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-20 22:07
 */
public interface BlogSortService extends SuperService<BlogSort> {
    /**
     * 获取博客分类列表
     *
     * @param blogSortVO
     * @return
     */
    IPage<BlogSort> getPageList(BlogSortVO blogSortVO);

    /**
     * 获取博客分类列表
     *
     * @return
     */
    List<BlogSort> getList();

    /**
     * 新增博客分类
     *
     * @param blogSortVO
     */
    String addBlogSort(BlogSortVO blogSortVO);

    /**
     * 编辑博客分类
     *
     * @param blogSortVO
     */
    String editBlogSort(BlogSortVO blogSortVO);

    /**
     * 批量删除博客分类
     *
     * @param blogSortVoList
     */
    String deleteBatchBlogSort(List<BlogSortVO> blogSortVoList);

    /**
     * 置顶博客分类
     *
     * @param blogSortVO
     */
    String stickBlogSort(BlogSortVO blogSortVO);

    /**
     * 通过点击量排序博客
     *
     * @return
     */
    String blogSortByClickCount();

    /**
     * 通过引用量排序博客
     *
     * @return
     */
    String blogSortByCite();

    /**
     * 获取排序最高的一个博客分类
     *
     * @return
     */
    BlogSort getTopOne();
}
