package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.Picture;
import com.huanghongbe.zoom.xo.vo.PictureVO;

import java.util.List;

/**
 * 图片表 服务类
 *
 */
public interface PictureService extends SuperService<Picture> {

    /**
     * 获取图片列表
     *
     * @param pictureVO
     * @return
     */
    IPage<Picture> getPageList(PictureVO pictureVO);

    /**
     * 新增图片
     *
     * @param pictureVOList
     * @return
     */
    String addPicture(List<PictureVO> pictureVOList);

    /**
     * 编辑图片
     *
     * @param pictureVO
     * @return
     */
    String editPicture(PictureVO pictureVO);

    /**
     * 批量删除图片
     *
     * @param pictureVO
     */
    String deleteBatchPicture(PictureVO pictureVO);

    /**
     * 设置图片封面
     *
     * @param pictureVO
     */
    String setPictureCover(PictureVO pictureVO);

    /**
     * 获取最新图片,按时间排序
     *
     * @return
     */
    Picture getTopOne();
}
