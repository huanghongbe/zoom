package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.Constants;
import com.huanghongbe.zoom.base.enums.EPublish;
import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.commons.entity.Tag;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.TagMapper;
import com.huanghongbe.zoom.xo.service.BlogService;
import com.huanghongbe.zoom.xo.service.TagService;
import com.huanghongbe.zoom.xo.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-18 23:03
 */
@Service
public class TagServiceImpl extends SuperServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagService tagService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public IPage<Tag> getPageList(TagVO tagVO) {
        QueryWrapper<Tag> queryWrapper=new QueryWrapper<>();
        if (StringUtils.isNotEmpty(tagVO.getKeyword()) && !StringUtils.isEmpty(tagVO.getKeyword())) {
            queryWrapper.eq(SQLConf.CONTENT,tagVO.getKeyword());
        }
//        IPage<Tag> page=new Page<>();
//        page.setCurrent(tagVO.getCurrentPage());
//        page.setSize(tagVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        if(StringUtils.isNotEmpty(tagVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(tagVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if(StringUtils.isNotEmpty(tagVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(tagVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.SORT);
        }
        IPage<Tag> pageList = tagService.page(new Page<>(tagVO.getCurrentPage(),tagVO.getPageSize()), queryWrapper);
        return pageList;
    }

    @Override
    public List<Tag> getList() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        return tagService.list(queryWrapper);
    }

    @Override
    public String addTag(TagVO tagVO) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.CONTENT, tagVO.getContent());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        Tag tempTag = tagService.getOne(queryWrapper);
        if (tempTag != null) {
            return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
        }
        Tag tag = new Tag();
        tag.setContent(tagVO.getContent());
        tag.setClickCount(0);
        tag.setStatus(EStatus.ENABLE);
        tag.setSort(tagVO.getSort());
        tag.insert();
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editTag(TagVO tagVO) {
        Tag tag = tagService.getById(tagVO.getUid());
        if (tag != null && !tag.getContent().equals(tagVO.getContent())) {
            QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(SQLConf.CONTENT, tagVO.getContent());
            queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            Tag tempTag = tagService.getOne(queryWrapper);
            if (tempTag != null) {
                return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
            }
        }
        tag.setContent(tagVO.getContent());
        tag.setStatus(EStatus.ENABLE);
        tag.setSort(tagVO.getSort());
        tag.setUpdateTime(new Date());
        tag.updateById();
        // 删除和标签相关的博客缓存
        blogService.deleteRedisByBlogTag();
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchTag(List<TagVO> tagVOList) {
        if (tagVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        tagVOList.forEach(i->uids.add(i.getUid()));
        // 判断要删除的分类，是否有博客
        QueryWrapper<Blog> blogQueryWrapper = new QueryWrapper<>();
        blogQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        blogQueryWrapper.in(SQLConf.TAG_UID, uids);
        Integer blogCount = blogService.count(blogQueryWrapper);
        if (blogCount > 0) {
            return ResultUtil.errorWithMessage(MessageConf.BLOG_UNDER_THIS_TAG);
        }
        Collection<Tag> tagList = tagService.listByIds(uids);
        tagList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = tagService.updateBatchById(tagList);
        // 删除和标签相关的博客缓存
        blogService.deleteRedisByBlogTag();
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }

    @Override
    public String stickTag(TagVO tagVO) {
        Tag tag = tagService.getById(tagVO.getUid());
        //查找出最大的那一个
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConf.SORT);
        queryWrapper.last(SysConf.LIMIT_ONE);
//        IPage<Tag> pageList = tagService.page(new Page<>(0,1), queryWrapper);
//        List<Tag> list = pageList.getRecords();
//        Tag maxTag = list.get(0);
        Tag maxTag=tagService.getOne(queryWrapper);
        if (StringUtils.isEmpty(maxTag.getUid())) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        if (maxTag.getUid().equals(tag.getUid())) {
            return ResultUtil.errorWithMessage(MessageConf.THIS_TAG_IS_TOP);
        }
        Integer sortCount = maxTag.getSort() + 1;
        tag.setSort(sortCount);
        tag.setUpdateTime(new Date());
        tag.updateById();
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String tagSortByClickCount() {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        // 按点击从高到低排序
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        List<Tag> tagList = tagService.list(queryWrapper);
        // 设置初始化最大的sort值
        Integer maxSort = tagList.size();
        tagList.forEach(item->{
            item.setSort(item.getClickCount());
            item.setCreateTime(new Date());
        });
        tagService.updateBatchById(tagList);
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public String tagSortByCite() {
        // 定义Map   key：tagUid,  value: 引用量
        Map<String, Integer> map = new HashMap<>();
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        List<Tag> tagList = tagService.list(tagQueryWrapper);
        // 初始化所有标签的引用量
        tagList.forEach(item -> {
            map.put(item.getUid(), 0);
        });
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        // 过滤content字段
        queryWrapper.select(Blog.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        List<Blog> blogList = blogService.list(queryWrapper);
        blogList.forEach(item -> {
            String tagUids = item.getTagUid();
            List<String> tagUidList = StringUtils.changeStringToString(tagUids, SysConf.FILE_SEGMENTATION);
            tagUidList.forEach(tagUid->{
                if (map.get(tagUid) != null) {
                    Integer count = map.get(tagUid) + 1;
                    map.put(tagUid, count);
                } else {
                    map.put(tagUid, 0);
                }
            });
        });

        tagList.forEach(item -> {
            item.setSort(map.get(item.getUid()));
            item.setUpdateTime(new Date());
        });
        tagService.updateBatchById(tagList);
        // 删除Redis中的BLOG_TAG
        deleteRedisBlogTagList();
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }

    @Override
    public List<Tag> getHotTag(Integer hotTagCount) {
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
//        Page<Tag> page = new Page<>();
//        page.setCurrent(1);
//        page.setSize(hotTagCount);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        IPage<Tag> pageList = tagService.page(new Page<>(1,hotTagCount), queryWrapper);
        return pageList.getRecords();
    }

    @Override
    public Tag getTopTag() {
        QueryWrapper<Tag> tagQueryWrapper = new QueryWrapper<>();
        tagQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        tagQueryWrapper.last(SysConf.LIMIT_ONE);
        tagQueryWrapper.orderByDesc(SQLConf.SORT);
        return tagService.getOne(tagQueryWrapper);
    }

    /**
     * 删除Redis中的标签列表
     */
    private void deleteRedisBlogTagList() {
        // 删除Redis中的BLOG_LINK
        Set<String> keys = redisUtil.keys(RedisConf.BLOG_TAG + Constants.SYMBOL_COLON + "*");
        redisUtil.delete(keys);
    }
}
