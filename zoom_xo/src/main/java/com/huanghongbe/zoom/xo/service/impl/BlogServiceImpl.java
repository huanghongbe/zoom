package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.*;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.utils.JsonUtils;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.BlogMapper;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.vo.BlogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:14
 */
@Service
public class BlogServiceImpl extends SuperServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private BlogService blogService;
    @Resource
    private BlogMapper blogMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public List<Blog> setTagByBlogList(List<Blog> list) {
        List<Blog> notNullList=list.stream().filter(Objects::nonNull).collect(Collectors.toList());
        notNullList.forEach(this::setTagByBlog);
        return notNullList;
    }

    @Override
    public List<Blog> setTagAndSortByBlogList(List<Blog> list) {
        return null;
    }

    @Override
    public List<Blog> setTagAndSortAndPictureByBlogList(List<Blog> list) {
        return null;
    }

    @Override
    public Blog setTagByBlog(Blog blog) {
        return null;
    }

    @Override
    public Blog setSortByBlog(Blog blog) {
        return null;
    }

    @Override
    public List<Blog> getBlogListByLevel(Integer level) {
        QueryWrapper<Blog> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.LEVEL,level);
        queryWrapper.eq(BaseSQLConf.STATUS,EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH,EPublish.PUBLISH);
        return blogMapper.selectList(queryWrapper);
    }

    @Override
    public IPage<Blog> getBlogPageByLevel(Page<Blog> page, Integer level, Integer useSort) {
        QueryWrapper<Blog> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.LEVEL,level);
        queryWrapper.eq(BaseSQLConf.STATUS,EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH,EPublish.PUBLISH);
        if (useSort==0){
            queryWrapper.orderByDesc(BaseSQLConf.CREATE_TIME);
        }else {
            queryWrapper.orderByDesc(BaseSQLConf.SORT);
        }
        queryWrapper.select(Blog.class,i->!i.getProperty().equals(SysConf.CONTENT));
        return blogMapper.selectPage(page,queryWrapper);
    }

    @Override
    public Integer getBlogCount(Integer status) {
        QueryWrapper<Blog> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.STATUS,EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        return blogMapper.selectCount(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> getBlogCountByTag() {
        //从redis中获取
        return null;
    }

    @Override
    public List<Map<String, Object>> getBlogCountByBlogSort() {
        return null;
    }

    @Override
    public Map<String, Object> getBlogContributeCount() {
        return null;
    }

    @Override
    public Blog getBlogByUid(String uid) {
        Blog blog=blogService.getBlogByUid(uid);
        if(blog!=null&&blog.getStatus()!=EStatus.DISABLED){
            //给博客是指标签和分类
            blog=setTagByBlog(blog);
            blog=setSortByBlog(blog);
            return blog;
        }
        return null;
    }

    @Override
    public List<Blog> getSameBlogByBlogUid(String blogUid) {
        Blog blog=blogService.getById(blogUid);
        QueryWrapper<Blog> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS,EStatus.ENABLE);
        //Page<Blog> page=new Page<>(1,10);
        queryWrapper.eq(SQLConf.BLOG_SORT_UID,blog.getBlogSortUid());
        queryWrapper.eq(SQLConf.IS_PUBLISH,EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.last(SysConf.LIMIT_TEN);
        //IPage<Blog> pageList=blogService.page(page,queryWrapper);
        List<Blog>list=blogService.setTagAndSortByBlogList(blogService.list(queryWrapper));
        return list.stream().filter(i->!i.getUid().equals(blogUid)).collect(Collectors.toList());
    }

    @Override
    public List<Blog> getBlogListByTop(Integer top) {
        QueryWrapper<Blog> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH,EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.SORT);
        queryWrapper.last(SysConf.LIMIT+top);
        return blogService.setTagAndSortAndPictureByBlogList(blogService.list(queryWrapper));
    }

    @Override
    public IPage<Blog> getPageList(BlogVO blogVO) {
        return null;
    }

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

    @Override
    public String editBlog(BlogVO blogVO) {
        return null;
    }

    @Override
    public String editBatch(List<BlogVO> blogVOList) {
        return null;
    }

    public String deleteBlog(BlogVO blogVO){
        Blog blog=blogService.getById(blogVO.getUid());
        blog.setStatus(EStatus.DISABLED);
        Boolean save=updateById(blog);
        if(save){
            //mq打入消息,redis消费
        }
        return ResultUtil.successWithData(MessageConf.DELETE_SUCCESS);

    }

    @Override
    public String deleteBatchBlog(List<BlogVO> blogVoList) {
        if(blogVoList.size()<=0){
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uidList=new ArrayList<>();
        StringBuffer stringBuffer=new StringBuffer();
        blogVoList.forEach(i->{
            uidList.add(i.getUid());
            stringBuffer.append(i.getUid()+SysConf.FILE_SEGMENTATION);
        });
        Collection<Blog> blogList = blogService.listByIds(uidList);
        blogList.forEach(i->{
            i.setStatus(EStatus.DISABLED);
        });
        Boolean save=blogService.updateBatchById(blogList);
        if(save){
            //MQ打入消息
        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String uploadLocalBlog(List<MultipartFile> filedatas) throws IOException {
        return null;
    }

    @Override
    public void deleteRedisByBlogSort() {
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);
        deleteRedisByBlog();
    }

    @Override
    public void deleteRedisByBlogTag() {
        redisUtil.delete(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);
        deleteRedisByBlog();
    }

    @Override
    public void deleteRedisByBlog() {
        // 删除博客相关缓存
        redisUtil.delete(RedisConf.NEW_BLOG);
        redisUtil.delete(RedisConf.HOT_BLOG);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.FIRST);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.SECOND);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.THIRD);
        redisUtil.delete(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + ELevel.FOURTH);
    }

    @Override
    public IPage<Blog> getBlogPageByLevel(Integer level, Long currentPage, Integer useSort) {
        return null;
    }

    @Override
    public IPage<Blog> getHotBlog() {
        return null;
    }

    @Override
    public IPage<Blog> getNewBlog(Long currentPage, Long pageSize) {
        return null;
    }

    @Override
    public IPage<Blog> getBlogBySearch(Long currentPage, Long pageSize) {
        return null;
    }

    @Override
    public IPage<Blog> getBlogByTime(Long currentPage, Long pageSize) {
        QueryWrapper<Blog> queryWrapper=new QueryWrapper<>();
        IPage<Blog> page=new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.eq(SQLConf.STATUS,EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH,EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.select(Blog.class,i->!i.getProperty().equals(SQLConf.CONTENT));
        IPage<Blog> pageList=blogService.page(page,queryWrapper);
        /**
         * 设置博客的分类标签和内容
         */
        return pageList;
    }

    @Override
    public Integer getBlogPraiseCountByUid(String uid) {
        Integer praiseCount=0;
        if(StringUtils.isEmpty(uid)){
            return praiseCount;
        }
        String JsonResult=redisUtil.get(RedisConf.BLOG_PRAISE + RedisConf.SEGMENTATION + uid);
        if(StringUtils.isNotEmpty(JsonResult)){
            praiseCount=Integer.parseInt(JsonResult);
        }
        return praiseCount;
    }

    @Override
    public String praiseBlogByUid(String uid) {
        if(StringUtils.isEmpty(uid)){
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        if (request.getAttribute(SysConf.USER_UID) != null) {
            //判断该用户是否已经点赞
        }else {
            return ResultUtil.errorWithMessage(MessageConf.PLEASE_LOGIN_TO_PRISE);
        }
        Blog blog=blogService.getById(uid);
        String pariseJsonResult = redisUtil.get(RedisConf.BLOG_PRAISE + RedisConf.SEGMENTATION + uid);
        if(StringUtils.isEmpty(pariseJsonResult)){
            redisUtil.set(RedisConf.BLOG_PRAISE + RedisConf.SEGMENTATION + uid, "1");
            blog.setCollectCount(1);
        }else {
            redisUtil.set(RedisConf.BLOG_PRAISE+RedisConf.SEGMENTATION+uid,String.valueOf(blog.getPraiseCount()+1));
            blog.setPraiseCount(blog.getPraiseCount()+1);
        }
        blogService.updateById(blog);
        /**
         * 如果是已登录用户，向评论表添加记录
         */
        return ResultUtil.successWithData(blog.getCollectCount());
    }

    @Override
    public IPage<Blog> getSameBlogByTagUid(String tagUid) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(1);
        page.setSize(10);
        queryWrapper.like(SQLConf.TAG_UID, tagUid);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortByBlogList(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getListByBlogSortUid(String blogSortUid, Long currentPage, Long pageSize) {
        //分页
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(SQLConf.BLOG_SORT_UID, blogSortUid);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        //给博客增加标签和分类
        List<Blog> list = blogService.setTagAndSortAndPictureByBlogList(pageList.getRecords());
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public Map<String, Object> getBlogByKeyword(String keywords, Long currentPage, Long pageSize) {
        return null;
    }

    @Override
    public IPage<Blog> searchBlogByTag(String tagUid, Long currentPage, Long pageSize) {
        return null;
    }

    @Override
    public IPage<Blog> searchBlogByBlogSort(String blogSortUid, Long currentPage, Long pageSize) {
        return null;
    }

    @Override
    public IPage<Blog> searchBlogByAuthor(String author, Long currentPage, Long pageSize) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.eq(SQLConf.AUTHOR, author);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SysConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortAndPictureByBlogList(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String getBlogTimeSortList() {
        //从Redis中获取内容
        String monthResult = redisUtil.get(SysConf.MONTH_SET);
        //判断redis中时候包含归档的内容
        if (StringUtils.isNotEmpty(monthResult)) {
            List list = JsonUtils.jsonArrayToArrayList(monthResult);
            return ResultUtil.successWithData(list);
        }
        // 第一次启动的时候归档
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        List<Blog> list = blogService.list(queryWrapper);
        //给博客增加标签、分类、图片
        list = blogService.setTagAndSortAndPictureByBlogList(list);
        Map<String, List<Blog>> map = new HashMap<>();
        Set<String> monthSet = new TreeSet<>();
        list.forEach(blog->{
            Date createTime = blog.getCreateTime();
            String month = new SimpleDateFormat("yyyy年MM月").format(createTime).toString();
            monthSet.add(month);
            if (map.get(month) == null) {
                List<Blog> blogList = new ArrayList<>();
                blogList.add(blog);
                map.put(month, blogList);
            } else {
                List<Blog> blogList = map.get(month);
                blogList.add(blog);
                map.put(month, blogList);
            }
        });
        // 缓存该月份下的所有文章  key: 月份   value：月份下的所有文章
        map.forEach((key, value) -> {
            redisUtil.set(SysConf.BLOG_SORT_BY_MONTH + SysConf.REDIS_SEGMENTATION + key, JsonUtils.objectToJson(value).toString());
        });
        //将从数据库查询的数据缓存到redis中
        redisUtil.set(SysConf.MONTH_SET, JsonUtils.objectToJson(monthSet).toString());
        return ResultUtil.successWithData(monthSet);
    }

    @Override
    public String getArticleByMonth(String monthDate) {
        if (StringUtils.isEmpty(monthDate)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        //从Redis中获取内容
        String contentResult = redisUtil.get(SysConf.BLOG_SORT_BY_MONTH + SysConf.REDIS_SEGMENTATION + monthDate);

        //判断redis中时候包含该日期下的文章
        if (StringUtils.isNotEmpty(contentResult)) {
            List list = JsonUtils.jsonArrayToArrayList(contentResult);
            return ResultUtil.successWithData(list);
        }

        // 第一次启动的时候归档
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        List<Blog> list = blogService.list(queryWrapper);
        //给博客增加标签、分类、图片
        list = blogService.setTagAndSortAndPictureByBlogList(list);
        Map<String, List<Blog>> map = new HashMap<>();
        Set<String> monthSet = new TreeSet<>();
        list.forEach(blog -> {
            Date createTime = blog.getCreateTime();
            String month = new SimpleDateFormat("yyyy年MM月").format(createTime).toString();
            monthSet.add(month);
            if (map.get(month) == null) {
                List<Blog> blogList = new ArrayList<>();
                blogList.add(blog);
                map.put(month, blogList);
            } else {
                List<Blog> blogList = map.get(month);
                blogList.add(blog);
                map.put(month, blogList);
            }
        });
        // 缓存该月份下的所有文章  key: 月份   value：月份下的所有文章
        map.forEach((key, value) -> {
            redisUtil.set(SysConf.BLOG_SORT_BY_MONTH + SysConf.REDIS_SEGMENTATION + key, JsonUtils.objectToJson(value).toString());
        });
        //将从数据库查询的数据缓存到redis中
        redisUtil.set(SysConf.MONTH_SET, JsonUtils.objectToJson(monthSet));
        return ResultUtil.successWithData(map.get(monthDate));
    }
}
