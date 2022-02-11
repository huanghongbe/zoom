package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.global.BaseSQLConf;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Comment;
import com.huanghongbe.zoom.xo.mapper.CommentMapper;
import com.huanghongbe.zoom.xo.service.CommentService;
import com.huanghongbe.zoom.xo.vo.CommentVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-07 23:20
 */
@Service
public class CommentServiceImpl extends SuperServiceImpl<CommentMapper, Comment> implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Override
    public Integer getCommentCount(int status) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.STATUS, status);
        return commentMapper.selectCount(queryWrapper);
    }

    @Override
    public IPage<Comment> getPageList(CommentVO commentVO) {
        return null;
    }

    @Override
    public String addComment(CommentVO commentVO) {
        return null;
    }

    @Override
    public String editComment(CommentVO commentVO) {
        return null;
    }

    @Override
    public String deleteComment(CommentVO commentVO) {
        return null;
    }

    @Override
    public String deleteBatchComment(List<CommentVO> commentVOList) {
        return null;
    }

    @Override
    public String batchDeleteCommentByBlogUid(List<String> blogUidList) {
        return null;
    }
}
