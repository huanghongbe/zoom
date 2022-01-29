package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Subject;
import com.huanghongbe.zoom.xo.mapper.SubjectMapper;
import com.huanghongbe.zoom.xo.service.SubjectService;
import com.huanghongbe.zoom.xo.vo.SubjectVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:46
 */
@Service
public class SubjectServiceImpl extends SuperServiceImpl<SubjectMapper, Subject> implements SubjectService {
    @Override
    public IPage<Subject> getPageList(SubjectVO subjectVO) {
        return null;
    }

    @Override
    public String addSubject(SubjectVO subjectVO) {
        return null;
    }

    @Override
    public String editSubject(SubjectVO subjectVO) {
        return null;
    }

    @Override
    public String deleteBatchSubject(List<SubjectVO> subjectVOList) {
        return null;
    }
}
