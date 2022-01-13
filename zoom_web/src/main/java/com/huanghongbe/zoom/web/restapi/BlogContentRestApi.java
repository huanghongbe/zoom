package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.vo.BlogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:56
 */
@RestController
@RequestMapping("/ontent")
public class BlogContentRestApi {

    @Autowired
    private BlogService blogService;


    @RequestMapping("/test/add")
    public String add(BlogVO blogVO){
        return blogService.addBlog(blogVO);
    }

    @RequestMapping("/test/delete")
    public String delete(BlogVO blogVO){
        return blogService.deleteBlog(blogVO);
    }
}
