package com.huanghongbe.zoom.xo.service;


import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.xo.vo.BlogVO;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:14
 */
public interface BlogService extends SuperService<Blog> {
    String addBlog(BlogVO blogVO);

    String deleteBlog(BlogVO blogVO);
}
