package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.*;
import com.huanghongbe.zoom.base.global.BaseSQLConf;
import com.huanghongbe.zoom.base.global.BaseSysConf;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.commons.entity.BlogSort;
import com.huanghongbe.zoom.commons.entity.Tag;
import com.huanghongbe.zoom.commons.feign.PictureFeignClient;
import com.huanghongbe.zoom.utils.*;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.BlogMapper;
import com.huanghongbe.zoom.xo.mapper.BlogSortMapper;
import com.huanghongbe.zoom.xo.mapper.TagMapper;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.service.BlogSortService;
import com.huanghongbe.zoom.xo.service.SysParamsService;
import com.huanghongbe.zoom.xo.service.TagService;
import com.huanghongbe.zoom.xo.utils.WebUtil;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 23:14
 */
@Service
public class BlogServiceImpl extends SuperServiceImpl<BlogMapper, Blog> implements BlogService {

    @Autowired
    private BlogSortService blogSortService;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private TagService tagService;
    @Autowired
    private BlogService blogService;
    @Resource
    private BlogMapper blogMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private TagMapper tagMapper;
    @Resource
    private BlogSortMapper blogSortMapper;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private PictureFeignClient pictureFeignClient;
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
        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();
        Set<String> fileUidSet = new HashSet<>();

        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUidSet.add(item.getFileUid());
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                // tagUid有多个，还需要切分
                if (StringUtils.isNotEmpty(item.getTagUid())) {
                    List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), BaseSysConf.FILE_SEGMENTATION);
                    for (String itemTagUid : tagUidsTemp) {
                        tagUids.add(itemTagUid);
                    }
                }
            }
        });

        String pictureList = null;
        StringBuffer fileUids = new StringBuffer();
        List<Map<String, Object>> picList = new ArrayList<>();
        // feign分页查询图片数据
        if(fileUidSet.size() > 0) {
            int count = 1;
            for(String fileUid: fileUidSet) {
                fileUids.append(fileUid + ",");
                System.out.println(count%10);
                if(count%10 == 0) {
                    pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), ",");
                    List<Map<String, Object>> tempPicList = webUtil.getPictureMap(pictureList);
                    picList.addAll(tempPicList);
                    fileUids = new StringBuffer();
                }
                count ++;
            }
            // 判断是否存在图片需要获取
            if(fileUids.length() >= Constants.NUM_32) {
                pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), Constants.SYMBOL_COMMA);
                List<Map<String, Object>> tempPicList = webUtil.getPictureMap(pictureList);
                picList.addAll(tempPicList);
            }
        }

        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }
        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = new HashMap<>();

        sortList.forEach(item -> {
            sortMap.put(item.getUid(), item);
        });

        tagList.forEach(item -> {
            tagMap.put(item.getUid(), item);
        });

        picList.forEach(item -> {
            pictureMap.put(item.get(SysConf.UID).toString(), item.get(SysConf.URL).toString());
        });

        for (Blog item : list) {
            //设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {

                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
                if (sortMap.get(item.getBlogSortUid()) != null) {
                    item.setBlogSortName(sortMap.get(item.getBlogSortUid()).getSortName());
                }
            }

            //获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), ",");
                List<Tag> tagListTemp = new ArrayList<Tag>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setTagList(tagListTemp);
            }

            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), Constants.SYMBOL_COMMA);
                List<String> pictureListTemp = new ArrayList<String>();

                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
                // 只设置一张标题图
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                } else {
                    item.setPhotoUrl("");
                }
            }
        }
        return list;
    }

    @Override
    public Blog setTagByBlog(Blog blog) {
        String tagUid = blog.getTagUid();
        if (!StringUtils.isEmpty(tagUid)) {
            String[] uids = tagUid.split(SysConf.FILE_SEGMENTATION);
            List<Tag> tagList = new ArrayList<>();
            for (String uid : uids) {
                Tag tag = tagMapper.selectById(uid);
                if (tag != null && tag.getStatus() != EStatus.DISABLED) {
                    tagList.add(tag);
                }
            }
            blog.setTagList(tagList);
        }
        return blog;
    }

    @Override
    public Blog setSortByBlog(Blog blog) {
        if (blog != null && !StringUtils.isEmpty(blog.getBlogSortUid())) {
            BlogSort blogSort = blogSortMapper.selectById(blog.getBlogSortUid());
            blog.setBlogSort(blogSort);
        }
        return blog;
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
        // 从Redis中获取标签下包含的博客数量
        String jsonArrayList = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG);
        if (StringUtils.isNotEmpty(jsonArrayList)) {
            ArrayList jsonList = JsonUtils.jsonArrayToArrayList(jsonArrayList);
            return jsonList;
        }

        List<Map<String, Object>> blogCoutByTagMap = blogMapper.getBlogCountByTag();
        Map<String, Integer> tagMap = new HashMap<>();
        for (Map<String, Object> item : blogCoutByTagMap) {
            String tagUid = String.valueOf(item.get(SQLConf.TAG_UID));
            // java.lang.Number是Integer,Long的父类
            Number num = (Number) item.get(SysConf.COUNT);
            Integer count = num.intValue();
            //如果只有一个UID的情况
            if (tagUid.length() == 32) {
                //如果没有这个内容的话，就设置
                if (tagMap.get(tagUid) == null) {
                    tagMap.put(tagUid, count);
                } else {
                    Integer tempCount = tagMap.get(tagUid) + count;
                    tagMap.put(tagUid, tempCount);
                }
            } else {
                //如果长度大于32，说明含有多个UID
                if (StringUtils.isNotEmpty(tagUid)) {
                    List<String> strList = StringUtils.changeStringToString(tagUid, ",");
                    for (String strItem : strList) {
                        if (tagMap.get(strItem) == null) {
                            tagMap.put(strItem, count);
                        } else {
                            Integer tempCount = tagMap.get(strItem) + count;
                            tagMap.put(strItem, tempCount);
                        }
                    }
                }
            }
        }

        //把查询到的Tag放到Map中
        Set<String> tagUids = tagMap.keySet();
        Collection<Tag> tagCollection = new ArrayList<>();
        if (tagUids.size() > 0) {
            tagCollection = tagMapper.selectBatchIds(tagUids);
        }

        Map<String, String> tagEntityMap = new HashMap<>();
        for (Tag tag : tagCollection) {
            if (StringUtils.isNotEmpty(tag.getContent())) {
                tagEntityMap.put(tag.getUid(), tag.getContent());
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            String tagUid = entry.getKey();
            if (tagEntityMap.get(tagUid) != null) {
                String tagName = tagEntityMap.get(tagUid);
                Integer count = entry.getValue();
                Map<String, Object> itemResultMap = new HashMap<>();
                itemResultMap.put(SysConf.TAG_UID, tagUid);
                itemResultMap.put(SysConf.NAME, tagName);
                itemResultMap.put(SysConf.VALUE, count);
                resultList.add(itemResultMap);
            }
        }
        // 将 每个标签下文章数目 存入到Redis【过期时间2小时】
        if (resultList.size() > 0) {
            redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_TAG, JsonUtils.objectToJson(resultList), 2, TimeUnit.HOURS);
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> getBlogCountByBlogSort() {
        // 从Redis中获取博客分类下包含的博客数量
        String jsonArrayList = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT);
        if (StringUtils.isNotEmpty(jsonArrayList)) {
            ArrayList jsonList = JsonUtils.jsonArrayToArrayList(jsonArrayList);
            return jsonList;
        }
        List<Map<String, Object>> blogCoutByBlogSortMap = blogMapper.getBlogCountByBlogSort();
        Map<String, Integer> blogSortMap = new HashMap<>();
        for (Map<String, Object> item : blogCoutByBlogSortMap) {

            String blogSortUid = String.valueOf(item.get(SQLConf.BLOG_SORT_UID));
            // java.lang.Number是Integer,Long的父类
            Number num = (Number) item.get(SysConf.COUNT);
            Integer count = 0;
            if (num != null) {
                count = num.intValue();
            }
            blogSortMap.put(blogSortUid, count);
        }

        //把查询到的BlogSort放到Map中
        Set<String> blogSortUids = blogSortMap.keySet();
        Collection<BlogSort> blogSortCollection = new ArrayList<>();

        if (blogSortUids.size() > 0) {
            blogSortCollection = blogSortMapper.selectBatchIds(blogSortUids);
        }

        Map<String, String> blogSortEntityMap = new HashMap<>();
        for (BlogSort blogSort : blogSortCollection) {
            if (StringUtils.isNotEmpty(blogSort.getSortName())) {
                blogSortEntityMap.put(blogSort.getUid(), blogSort.getSortName());
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Map.Entry<String, Integer> entry : blogSortMap.entrySet()) {

            String blogSortUid = entry.getKey();

            if (blogSortEntityMap.get(blogSortUid) != null) {
                String blogSortName = blogSortEntityMap.get(blogSortUid);
                Integer count = entry.getValue();
                Map<String, Object> itemResultMap = new HashMap<>();
                itemResultMap.put(SysConf.BLOG_SORT_UID, blogSortUid);
                itemResultMap.put(SysConf.NAME, blogSortName);
                itemResultMap.put(SysConf.VALUE, count);
                resultList.add(itemResultMap);
            }
        }
        // 将 每个分类下文章数目 存入到Redis【过期时间2小时】
        if (resultList.size() > 0) {
            redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_COUNT_BY_SORT, JsonUtils.objectToJson(resultList), 2, TimeUnit.HOURS);
        }
        return resultList;
    }

    @Override
    public Map<String, Object> getBlogContributeCount() {
        // 从Redis中获取博客分类下包含的博客数量
        String jsonMap = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT);
        if (StringUtils.isNotEmpty(jsonMap)) {
            Map<String, Object> resultMap = JsonUtils.jsonToMap(jsonMap);
            return resultMap;
        }

        // 获取今天结束时间
        String endTime = DateUtils.getNowTime();
        // 获取365天前的日期
        Date temp = DateUtils.getDate(endTime, -365);
        String startTime = DateUtils.dateTimeToStr(temp);
        List<Map<String, Object>> blogContributeMap = blogMapper.getBlogContributeCount(startTime, endTime);
        List<String> dateList = DateUtils.getDayBetweenDates(startTime, endTime);
        Map<String, Object> dateMap = new HashMap<>();
        for (Map<String, Object> itemMap : blogContributeMap) {
            dateMap.put(itemMap.get("DATE").toString(), itemMap.get("COUNT"));
        }

        List<List<Object>> resultList = new ArrayList<>();
        for (String item : dateList) {
            Integer count = 0;
            if (dateMap.get(item) != null) {
                count = Integer.valueOf(dateMap.get(item).toString());
            }
            List<Object> objectList = new ArrayList<>();
            objectList.add(item);
            objectList.add(count);
            resultList.add(objectList);
        }

        Map<String, Object> resultMap = new HashMap<>(Constants.NUM_TWO);
        List<String> contributeDateList = new ArrayList<>();
        contributeDateList.add(startTime);
        contributeDateList.add(endTime);
        resultMap.put(SysConf.CONTRIBUTE_DATE, contributeDateList);
        resultMap.put(SysConf.BLOG_CONTRIBUTE_COUNT, resultList);
        // 将 全年博客贡献度 存入到Redis【过期时间2小时】
        redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.BLOG_CONTRIBUTE_COUNT, JsonUtils.objectToJson(resultMap), 2, TimeUnit.HOURS);
        return resultMap;
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
        //从Redis中获取内容
        String jsonResult = redisUtil.get(RedisConf.BLOG_LEVEL + RedisConf.SEGMENTATION + level);

        //判断redis中是否有文章
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            IPage pageList = new Page();
            pageList.setRecords(jsonResult2List);
            return pageList;
        }
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        String blogCount = null;
        switch (level) {
            case ELevel.NORMAL: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
            }
            break;
            case ELevel.FIRST: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_FIRST_COUNT);
            }
            break;
            case ELevel.SECOND: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_SECOND_COUNT);
            }
            break;
            case ELevel.THIRD: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_THIRD_COUNT);
            }
            break;
            case ELevel.FOURTH: {
                blogCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_FOURTH_COUNT);
            }
            break;
        }
        if (StringUtils.isEmpty(blogCount)) {
            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
        } else {
            page.setSize(Long.valueOf(blogCount));
        }

        IPage<Blog> pageList = blogService.getBlogPageByLevel(page, level, useSort);
        List<Blog> list = pageList.getRecords();

        // 一级推荐或者二级推荐没有内容时，自动把top5填充至一级推荐和二级推荐中
        if ((level == SysConf.ONE || level == SysConf.TWO) && list.size() == 0) {
            QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
            Page<Blog> hotPage = new Page<>();
            hotPage.setCurrent(1);
            String blogHotCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_HOT_COUNT);
            String blogSecondCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_SECOND_COUNT);
            if (StringUtils.isEmpty(blogHotCount) || StringUtils.isEmpty(blogSecondCount)) {
                log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
            } else {
                hotPage.setSize(Long.valueOf(blogHotCount));
            }
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
            queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
            queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
            IPage<Blog> hotPageList = blogService.page(hotPage, queryWrapper);
            List<Blog> hotBlogList = hotPageList.getRecords();
            List<Blog> secondBlogList = new ArrayList<>();
            List<Blog> firstBlogList = new ArrayList<>();
            for (int a = 0; a < hotBlogList.size(); a++) {
                // 当推荐大于两个的时候
                if ((hotBlogList.size() - firstBlogList.size()) > Long.valueOf(blogSecondCount)) {
                    firstBlogList.add(hotBlogList.get(a));
                } else {
                    secondBlogList.add(hotBlogList.get(a));
                }
            }

            firstBlogList = setBlog(firstBlogList);
            secondBlogList = setBlog(secondBlogList);

            // 将从数据库查询的数据缓存到redis中，设置1小时后过期 [避免 list 中没有数据而保存至 redis 的情况]
            if (firstBlogList.size() > 0) {
                redisUtil.setEx(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_ONE, JsonUtils.objectToJson(firstBlogList), 1, TimeUnit.HOURS);
            }
            if (secondBlogList.size() > 0) {
                redisUtil.setEx(RedisConf.BLOG_LEVEL + Constants.SYMBOL_COLON + Constants.NUM_TWO, JsonUtils.objectToJson(secondBlogList), 1, TimeUnit.HOURS);
            }

            switch (level) {
                case SysConf.ONE: {
                    pageList.setRecords(firstBlogList);
                }
                break;
                case SysConf.TWO: {
                    pageList.setRecords(secondBlogList);
                }
                break;
            }
            return pageList;
        }

        list = setBlog(list);
        pageList.setRecords(list);

        // 将从数据库查询的数据缓存到redis中 [避免 list 中没有数据而保存至 redis 的情况]
        if (list.size() > 0) {
            redisUtil.setEx(SysConf.BLOG_LEVEL + SysConf.REDIS_SEGMENTATION + level, JsonUtils.objectToJson(list).toString(), 1, TimeUnit.HOURS);
        }
        return pageList;
    }

    @Override
    public IPage<Blog> getHotBlog() {
        //从Redis中获取内容
        String jsonResult = redisUtil.get(RedisConf.HOT_BLOG);
        //判断redis中是否有文章
        if (StringUtils.isNotEmpty(jsonResult)) {
            List jsonResult2List = JsonUtils.jsonArrayToArrayList(jsonResult);
            IPage pageList = new Page();
            pageList.setRecords(jsonResult2List);
            return pageList;
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(0);
        String blogHotCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_HOT_COUNT);
        if (StringUtils.isEmpty(blogHotCount)) {
            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
        } else {
            page.setSize(Long.valueOf(blogHotCount));
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = setBlog(list);
        pageList.setRecords(list);
        // 将从数据库查询的数据缓存到redis中[避免list中没有数据而保存至redis的情况]
        if (list.size() > 0) {
            redisUtil.setEx(RedisConf.HOT_BLOG, JsonUtils.objectToJson(list), 1, TimeUnit.HOURS);
        }
        return pageList;
    }

    @Override
    public IPage<Blog> getNewBlog(Long currentPage, Long pageSize) {
        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
        if (StringUtils.isEmpty(blogNewCount)) {
            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
        }

        // 判断Redis中是否缓存了第一页的内容
        if (currentPage == 1L) {
            //从Redis中获取内容
            String jsonResult = redisUtil.get(RedisConf.NEW_BLOG);
            //判断redis中是否有文章
            if (StringUtils.isNotEmpty(jsonResult)) {
                IPage pageList = JsonUtils.jsonToPojo(jsonResult, Page.class);
                return pageList;
            }
        }

        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(Long.valueOf(blogNewCount));
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);

        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));

        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();

        if (list.size() <= 0) {
            return pageList;
        }

        list = setBlog(list);
        pageList.setRecords(list);

        //将从最新博客缓存到redis中
        if (currentPage == 1L) {
            redisUtil.setEx(RedisConf.NEW_BLOG, JsonUtils.objectToJson(pageList), 1, TimeUnit.HOURS);
        }
        return pageList;
    }

    @Override
    public IPage<Blog> getBlogBySearch(Long currentPage, Long pageSize) {
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        Page<Blog> page = new Page<>();
        page.setCurrent(currentPage);
        String blogNewCount = sysParamsService.getSysParamsValueByKey(SysConf.BLOG_NEW_COUNT);
        if (StringUtils.isEmpty(blogNewCount)) {
            log.error(MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
        } else {
            page.setSize(Long.valueOf(blogNewCount));
        }
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<Blog> pageList = blogService.page(page, queryWrapper);
        List<Blog> list = pageList.getRecords();
        if (list.size() <= 0) {
            return pageList;
        }
        list = setBlog(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public IPage<Blog> getBlogByTime(Long currentPage, Long pageSize) {
        QueryWrapper<Blog> queryWrapper=new QueryWrapper<>();
//        IPage<Blog> page=new Page<>();
//        page.setCurrent(currentPage);
//        page.setSize(pageSize);
        queryWrapper.eq(SQLConf.STATUS,EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH,EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.select(Blog.class,i->!i.getProperty().equals(SQLConf.CONTENT));
        IPage<Blog> pageList=blogService.page(new Page<>(currentPage,pageSize),queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = setBlog(list);
        pageList.setRecords(list);
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
//        Page<Blog> page = new Page<>();
//        page.setCurrent(currentPage);
//        page.setSize(pageSize);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(SQLConf.BLOG_SORT_UID, blogSortUid);
        //因为首页并不需要显示内容，所以需要排除掉内容字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        IPage<Blog> pageList = blogService.page(new Page<>(currentPage,pageSize), queryWrapper);
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
        Tag tag = tagService.getById(tagUid);
        if (tag != null) {
            HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            String ip = IpUtils.getIpAddr(request);
            //从Redis取出数据，判断该用户24小时内，是否点击过该标签
            String jsonResult = redisUtil.get(RedisConf.TAG_CLICK + RedisConf.SEGMENTATION + ip + "#" + tagUid);
            if (StringUtils.isEmpty(jsonResult)) {
                //给标签点击数增加
                int clickCount = tag.getClickCount() + 1;
                tag.setClickCount(clickCount);
                tag.updateById();
                //将该用户点击记录存储到redis中, 24小时后过期
                redisUtil.setEx(RedisConf.TAG_CLICK + RedisConf.SEGMENTATION + ip + RedisConf.WELL_NUMBER + tagUid, clickCount + "",
                        24, TimeUnit.HOURS);
            }
        }
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
//        Page<Blog> page = new Page<>();
//        page.setCurrent(currentPage);
//        page.setSize(pageSize);

        queryWrapper.like(SQLConf.TAG_UID, tagUid);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SysConf.CONTENT));
        IPage<Blog> pageList = blogService.page(new Page<>(currentPage,pageSize), queryWrapper);
        List<Blog> list = pageList.getRecords();
        list = blogService.setTagAndSortAndPictureByBlogList(list);
        pageList.setRecords(list);
        return pageList;
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

    /**
     * 设置博客的分类标签和内容
     *
     * @param list
     * @return
     */
    private List<Blog> setBlog(List<Blog> list) {
        final StringBuffer fileUids = new StringBuffer();
        List<String> sortUids = new ArrayList<>();
        List<String> tagUids = new ArrayList<>();

        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + SysConf.FILE_SEGMENTATION);
            }
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                sortUids.add(item.getBlogSortUid());
            }
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                tagUids.add(item.getTagUid());
            }
        });
        String pictureList = null;

        if (fileUids != null) {
            pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        Collection<BlogSort> sortList = new ArrayList<>();
        Collection<Tag> tagList = new ArrayList<>();
        if (sortUids.size() > 0) {
            sortList = blogSortService.listByIds(sortUids);
        }
        if (tagUids.size() > 0) {
            tagList = tagService.listByIds(tagUids);
        }

        Map<String, BlogSort> sortMap = new HashMap<>();
        Map<String, Tag> tagMap = new HashMap<>();
        Map<String, String> pictureMap = new HashMap<>();

        sortList.forEach(item -> {
            sortMap.put(item.getUid(), item);
        });

        tagList.forEach(item -> {
            tagMap.put(item.getUid(), item);
        });

        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });


        for (Blog item : list) {

            //设置分类
            if (StringUtils.isNotEmpty(item.getBlogSortUid())) {
                item.setBlogSort(sortMap.get(item.getBlogSortUid()));
            }

            //获取标签
            if (StringUtils.isNotEmpty(item.getTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getTagUid(), SysConf.FILE_SEGMENTATION);
                List<Tag> tagListTemp = new ArrayList<Tag>();

                tagUidsTemp.forEach(tag -> {
                    if (tagMap.get(tag) != null) {
                        tagListTemp.add(tagMap.get(tag));
                    }
                });
                item.setTagList(tagListTemp);
            }

            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), SysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();

                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        return list;
    }

}
