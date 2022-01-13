package com.huanghongbe.zoom.xo.service.impl;

import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.xo.mapper.BlogMapper;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.vo.BlogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:14
 */
@Service
public class BlogServiceImpl extends SuperServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private BlogService blogService;

    public String addBlog(BlogVO blogVO){
        Blog blog=new Blog();
        blog.setTitle(blogVO.getTitle());
        blog.setSummary(blogVO.getSummary());
        blog.setContent(blogVO.getContent());
        blog.setTagUid(blogVO.getTagUid());
        blog.setBlogSortUid(blogVO.getBlogSortUid());
        blog.setFileUid(blogVO.getFileUid());
        blog.setLevel(blogVO.getLevel());
        blog.setIsOriginal(blogVO.getIsOriginal());
        blog.setIsPublish(blogVO.getIsPublish());
        blog.setType(blogVO.getType());
        blog.setOutsideLink(blogVO.getOutsideLink());
        blog.setStatus(EStatus.ENABLE);
        blog.setOpenComment(blogVO.getOpenComment());
        Boolean isSave=blogService.save(blog);
        if(isSave){
            return "success";
        }else {
            return "fail";
        }
    }
    public String deleteBlog(BlogVO blogVO){
        Blog blog=blogService.getById(blogVO.getUid());
        blog.setStatus(EStatus.DISABLED);
        Boolean save=updateById(blog);
        if(save){
            return "success";
        }else {
            return "fail";
        }
    }
}
