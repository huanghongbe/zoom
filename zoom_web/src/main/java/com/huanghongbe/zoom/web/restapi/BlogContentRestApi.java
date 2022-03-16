package com.huanghongbe.zoom.web.restapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.EPublish;
import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.base.global.ECode;
import com.huanghongbe.zoom.base.holder.RequestHolder;
import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.commons.feign.PictureFeignClient;
import com.huanghongbe.zoom.utils.IpUtils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.utils.WebUtil;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:56
 */
@RestController
@RefreshScope
@RequestMapping("/content")
public class BlogContentRestApi {

    @Autowired
    private WebUtil webUtil;
    @Autowired
    private BlogService blogService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value(value = "${BLOG.ORIGINAL_TEMPLATE}")
    private String ORIGINAL_TEMPLATE;
    @Value(value = "${BLOG.REPRINTED_TEMPLATE}")
    private String REPRINTED_TEMPLATE;
    @RequestMapping("/getBlogByUid")
    public String getBlogByUid(@RequestParam(value = "uid",required = false) String uid,
                              @RequestParam(value = "oid",required = false,defaultValue = "0") Integer oid){
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        if (StringUtils.isEmpty(uid) && oid == 0) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        Blog blog = null;
        if (StringUtils.isNotEmpty(uid)) {
            blog = blogService.getById(uid);
        } else {
            QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SysConf.OID, oid);
            queryWrapper.last(SysConf.LIMIT_ONE);
            blog = blogService.getOne(queryWrapper);
        }

        if (blog == null || blog.getStatus() == EStatus.DISABLED || EPublish.NO_PUBLISH.equals(blog.getIsPublish())) {
            return ResultUtil.result(ECode.ERROR, MessageConf.BLOG_IS_DELETE);
        }

        // 设置文章版权申明
        setBlogCopyright(blog);

        //设置博客标签
        blogService.setTagByBlog(blog);

        //获取分类
        blogService.setSortByBlog(blog);

        //设置博客标题图
        setPhotoListByBlog(blog);

        //从Redis取出数据，判断该用户是否点击过
        String jsonResult = stringRedisTemplate.opsForValue().get("BLOG_CLICK:" + ip + "#" + blog.getUid());

        if (StringUtils.isEmpty(jsonResult)) {

            //给博客点击数增加
            Integer clickCount = blog.getClickCount() + 1;
            blog.setClickCount(clickCount);
            blog.updateById();

            //将该用户点击记录存储到redis中, 24小时后过期
            stringRedisTemplate.opsForValue().set(RedisConf.BLOG_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_WELL + blog.getUid(), blog.getClickCount().toString(),
                    24, TimeUnit.HOURS);
        }
        return ResultUtil.result(SysConf.SUCCESS, blog);
    }

    @GetMapping("/getBlogPraiseCountByUid")
    public String getBlogPraiseCountByUid(@RequestParam(name = "uid", required = false) String uid) {
        return ResultUtil.result("success", blogService.getBlogPraiseCountByUid(uid));
    }

    @GetMapping("/praiseBlogByUid")
    public String praiseBlogByUid(@RequestParam(name = "uid", required = false) String uid) {
        if (StringUtils.isEmpty(uid)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        return blogService.praiseBlogByUid(uid);
    }


    @GetMapping("/getSameBlogByTagUid")
    public String getSameBlogByTagUid(@RequestParam(name = "tagUid", required = true) String tagUid,
                                      @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        if (StringUtils.isEmpty(tagUid)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        return ResultUtil.result(SysConf.SUCCESS, blogService.getSameBlogByTagUid(tagUid));
    }


    @GetMapping("/getSameBlogByBlogUid")
    public String getSameBlogByBlogUid(@ApiParam(name = "blogUid", value = "博客标签UID", required = true) @RequestParam(name = "blogUid", required = true) String blogUid) {
        if (StringUtils.isEmpty(blogUid)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.PARAM_INCORRECT);
        }
        List<Blog> blogList = blogService.getSameBlogByBlogUid(blogUid);
        IPage<Blog> pageList = new Page<>();
        pageList.setRecords(blogList);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    /**
     * 设置博客标题图
     *
     * @param blog
     */
    private void setPhotoListByBlog(Blog blog) {
        //获取标题图片
        if (blog != null && !StringUtils.isEmpty(blog.getFileUid())) {
            String result = this.pictureFeignClient.getPicture(blog.getFileUid(), Constants.SYMBOL_COMMA);
            List<String> picList = webUtil.getPicture(result);
            if (picList != null && picList.size() > 0) {
                blog.setPhotoList(picList);
            }
        }
    }

    /**
     * 设置博客版权
     *
     * @param blog
     */
    private void setBlogCopyright(Blog blog) {

        //如果是原创的话
        if (Constants.STR_ONE.equals(blog.getIsOriginal())) {
            blog.setCopyright(ORIGINAL_TEMPLATE);
        } else {
            String reprintedTemplate = REPRINTED_TEMPLATE;
            String[] variable = {blog.getArticlesPart(), blog.getAuthor()};
            String str = String.format(reprintedTemplate, variable);
            blog.setCopyright(str);
        }
    }
}
