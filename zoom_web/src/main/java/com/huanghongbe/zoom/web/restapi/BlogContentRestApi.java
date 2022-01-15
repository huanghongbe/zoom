package com.huanghongbe.zoom.web.restapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.ECode;
import com.huanghongbe.zoom.base.enums.EPublish;
import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.utils.IpUtils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.service.BlogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:56
 */
@RestController
@RequestMapping("/content")
public class BlogContentRestApi {

    @Autowired
    private BlogService blogService;

    @RequestMapping("/getBlogById")
    public String getBlogById(@RequestParam(value = "uid",required = false) String uid,
                              @RequestParam(value = "oid",required = false,defaultValue = "0") Integer oid){
        HttpServletRequest httpServletRequest=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip= IpUtils.getIpAddr(httpServletRequest);
        if(StringUtils.isEmpty(uid)&&oid==0){
            return ResultUtil.result("error","入参有误");
        }
        Blog blog=null;
        if(StringUtils.isNotEmpty(uid)){
            //使用uid查找
            blog=blogService.getById(uid);
        }else {
            QueryWrapper<Blog> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("oid",oid);
            queryWrapper.last("LIMIT 1");
            blog=blogService.getOne(queryWrapper);
        }
        if(blog==null||blog.getStatus()== EStatus.DISABLED||EPublish.NO_PUBLISH.equals(blog.getIsPublish())){
            return ResultUtil.result(ECode.ERROR,"入参有误");
        }
        /**
         *  设置文章版权申明
         *  设置博客标签
         *  获取分类
         *  设置博客标题图
         *  从Redis取出数据，判断该用户是否点击过
         */
        return ResultUtil.result("success", blog);
    }

    @GetMapping("/getBlogPraiseCountByUid")
    public String getBlogPraiseCountByUid(@RequestParam(name = "uid", required = false) String uid) {
        return null;
        //return ResultUtil.result("success", blogService.getBlogPraiseCountByUid(uid));
    }

    @GetMapping("/praiseBlogByUid")
    public String praiseBlogByUid(@RequestParam(name = "uid", required = false) String uid) {
        return null;
    }


    @GetMapping("/getSameBlogByTagUid")
    public String getSameBlogByTagUid(@RequestParam(name = "tagUid", required = true) String tagUid,
                                      @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
       return null;
    }


    @GetMapping("/getSameBlogByBlogUid")
    public String getSameBlogByBlogUid(@ApiParam(name = "blogUid", value = "博客标签UID", required = true) @RequestParam(name = "blogUid", required = true) String blogUid) {
       return null;
    }
}
