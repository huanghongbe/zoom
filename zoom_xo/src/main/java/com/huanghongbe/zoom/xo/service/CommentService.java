package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.Comment;
import com.huanghongbe.zoom.xo.vo.CommentVO;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-07 23:20
 */
public interface CommentService extends SuperService<Comment> {
    /**
     * 获取评论数目
     *
     * @author xzx19950624@qq.com
     * @date 2018年10月22日下午3:43:38
     */
    Integer getCommentCount(int status);

    /**
     * 获取评论列表
     *
     * @param commentVO
     * @return
     */
    IPage<Comment> getPageList(CommentVO commentVO);

    /**
     * 新增评论
     *
     * @param commentVO
     */
    String addComment(CommentVO commentVO);

    /**
     * 编辑评论
     *
     * @param commentVO
     */
    String editComment(CommentVO commentVO);

    /**
     * 删除评论
     *
     * @param commentVO
     */
    String deleteComment(CommentVO commentVO);

    /**
     * 批量删除评论
     *
     * @param commentVOList
     */
    String deleteBatchComment(List<CommentVO> commentVOList);

    /**
     *
     * @param blogUidList
     * @return
     */
    String batchDeleteCommentByBlogUid(List<String> blogUidList);
}
