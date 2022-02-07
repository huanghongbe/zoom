package com.huanghongbe.zoom.xo.service.impl;

import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Comment;
import com.huanghongbe.zoom.xo.mapper.CommentMapper;
import com.huanghongbe.zoom.xo.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-07 23:20
 */
@Service
public class CommentServiceImpl extends SuperServiceImpl<CommentMapper, Comment> implements CommentService {
}
