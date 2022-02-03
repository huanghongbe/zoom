package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.global.BaseSQLConf;
import com.huanghongbe.zoom.base.global.BaseSysConf;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Subject;
import com.huanghongbe.zoom.commons.entity.SubjectItem;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.SubjectMapper;
import com.huanghongbe.zoom.xo.service.SubjectItemService;
import com.huanghongbe.zoom.xo.service.SubjectService;
import com.huanghongbe.zoom.xo.utils.WebUtil;
import com.huanghongbe.zoom.xo.vo.SubjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:46
 */
@Service
public class SubjectServiceImpl extends SuperServiceImpl<SubjectMapper, Subject> implements SubjectService {
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private SubjectItemService subjectItemService;
//    @Resource
//    private PictureFeignClient pictureFeignClient;
    @Autowired
    private WebUtil webUtil;
    @Override
    public IPage<Subject> getPageList(SubjectVO subjectVO) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(subjectVO.getKeyword()) && !StringUtils.isEmpty(subjectVO.getKeyword().trim())) {
            queryWrapper.like(BaseSQLConf.SUBJECT_NAME, subjectVO.getKeyword().trim());
        }
        Page<Subject> page = new Page<>();
        page.setCurrent(subjectVO.getCurrentPage());
        page.setSize(subjectVO.getPageSize());
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(BaseSQLConf.SORT);
        IPage<Subject> pageList = subjectService.page(page, queryWrapper);
        List<Subject> list = pageList.getRecords();

        final StringBuffer fileUids = new StringBuffer();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + BaseSysConf.FILE_SEGMENTATION);
            }
        });
        String pictureResult = null;
        Map<String, String> pictureMap = new HashMap<>();
//        if (fileUids != null) {
//            pictureResult = this.pictureFeignClient.getPicture(fileUids.toString(), BaseSysConf.FILE_SEGMENTATION);
//        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureResult);
        picList.forEach(item -> {
            pictureMap.put(item.get(SysConf.UID).toString(), item.get(SysConf.URL).toString());
        });
        for (Subject item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getFileUid(), BaseSysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addSubject(SubjectVO subjectVO) {
        /**
         * 判断需要增加的分类是否存在
         */
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.SUBJECT_NAME, subjectVO.getSubjectName());
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(BaseSysConf.LIMIT_ONE);
        Subject tempSubject = subjectService.getOne(queryWrapper);
        if (tempSubject != null) {
            return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
        }
        Subject subject = new Subject();
        subject.setSubjectName(subjectVO.getSubjectName());
        subject.setSummary(subjectVO.getSummary());
        subject.setFileUid(subjectVO.getFileUid());
        subject.setClickCount(subjectVO.getClickCount());
        subject.setCollectCount(subjectVO.getCollectCount());
        subject.setSort(subjectVO.getSort());
        subject.setStatus(EStatus.ENABLE);
        subject.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editSubject(SubjectVO subjectVO) {
        Subject subject = subjectService.getById(subjectVO.getUid());
        /**
         * 判断需要编辑的分类是否存在
         */
        if (!subject.getSubjectName().equals(subjectVO.getSubjectName())) {
            QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(BaseSQLConf.SUBJECT_NAME, subjectVO.getSubjectName());
            queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
            Subject tempSubject = subjectService.getOne(queryWrapper);
            if (tempSubject != null) {
                return ResultUtil.errorWithMessage(MessageConf.ENTITY_EXIST);
            }
        }
        subject.setSubjectName(subjectVO.getSubjectName());
        subject.setSummary(subjectVO.getSummary());
        subject.setFileUid(subjectVO.getFileUid());
        subject.setClickCount(subjectVO.getClickCount());
        subject.setCollectCount(subjectVO.getCollectCount());
        subject.setSort(subjectVO.getSort());
        subject.setStatus(EStatus.ENABLE);
        subject.setUpdateTime(new Date());
        subject.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchSubject(List<SubjectVO> subjectVOList) {
        if (subjectVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = new ArrayList<>();
        subjectVOList.forEach(item -> {
            uids.add(item.getUid());
        });
        // 判断要删除的分类，是否有资源
        QueryWrapper<SubjectItem> subjectItemQueryWrapper = new QueryWrapper<>();
        subjectItemQueryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        subjectItemQueryWrapper.in(BaseSQLConf.SUBJECT_UID, uids);
        Integer count = subjectItemService.count(subjectItemQueryWrapper);
        if (count > 0) {
            return ResultUtil.errorWithMessage(MessageConf.SUBJECT_UNDER_THIS_SORT);
        }
        Collection<Subject> subjectList = subjectService.listByIds(uids);
        subjectList.forEach(item -> {
            item.setUpdateTime(new Date());
            item.setStatus(EStatus.DISABLED);
        });
        Boolean save = subjectService.updateBatchById(subjectList);
        if (save) {
            return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.DELETE_FAIL);
        }
    }
}
