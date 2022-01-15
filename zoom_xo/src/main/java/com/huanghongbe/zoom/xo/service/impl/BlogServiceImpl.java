package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.BaseSQLConf;
import com.huanghongbe.zoom.base.enums.EPublish;
import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.BlogMapper;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.vo.BlogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
            return "success";
        }else {
            return "fail";
        }
    }

    @Override
    public String deleteBatchBlog(List<BlogVO> blogVoList) {
        return null;
    }

    @Override
    public String uploadLocalBlog(List<MultipartFile> filedatas) throws IOException {
        return null;
    }

    @Override
    public void deleteRedisByBlogSort() {

    }

    @Override
    public void deleteRedisByBlogTag() {

    }

    @Override
    public void deleteRedisByBlog() {

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
        return null;
    }

    @Override
    public Integer getBlogPraiseCountByUid(String uid) {
        return null;
    }

    @Override
    public String praiseBlogByUid(String uid) {
        return null;
    }

    @Override
    public IPage<Blog> getSameBlogByTagUid(String tagUid) {
        return null;
    }

    @Override
    public IPage<Blog> getListByBlogSortUid(String blogSortUid, Long currentPage, Long pageSize) {
        return null;
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
        return null;
    }

    @Override
    public String getBlogTimeSortList() {
        return null;
    }

    @Override
    public String getArticleByMonth(String monthDate) {
        return null;
    }
}
