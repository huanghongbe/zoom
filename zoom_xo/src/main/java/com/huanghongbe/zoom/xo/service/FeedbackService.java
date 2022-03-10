package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Feedback;
import com.huanghongbe.zoom.xo.mapper.FeedbackMapper;
import com.huanghongbe.zoom.xo.vo.FeedbackVO;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-03-11 5:01
 */
public interface FeedbackService extends SuperService<Feedback> {

    /**
     * 获取反馈列表
     *
     * @param feedbackVO
     * @return
     */
    IPage<Feedback> getPageList(FeedbackVO feedbackVO);

    /**
     * 新增反馈
     *
     * @param feedbackVO
     */
    String addFeedback(FeedbackVO feedbackVO);

    /**
     * 编辑反馈
     *
     * @param feedbackVO
     */
    String editFeedback(FeedbackVO feedbackVO);

    /**
     * 批量删除反馈
     *
     * @param feedbackVOList
     */
    String deleteBatchFeedback(List<FeedbackVO> feedbackVOList);
}
