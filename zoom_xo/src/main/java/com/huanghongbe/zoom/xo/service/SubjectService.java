package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.Subject;
import com.huanghongbe.zoom.xo.vo.SubjectVO;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:44
 */
public interface SubjectService extends SuperService<Subject> {
    /**
     * 获取专题列表
     *
     * @param subjectVO
     * @return
     */
    IPage<Subject> getPageList(SubjectVO subjectVO);

    /**
     * 新增专题
     *
     * @param subjectVO
     */
    String addSubject(SubjectVO subjectVO);

    /**
     * 编辑专题
     *
     * @param subjectVO
     */
    String editSubject(SubjectVO subjectVO);

    /**
     * 批量删除专题
     *
     * @param subjectVOList
     */
    String deleteBatchSubject(List<SubjectVO> subjectVOList);
}
