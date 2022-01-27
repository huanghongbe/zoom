package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.Link;
import com.huanghongbe.zoom.xo.vo.LinkVO;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-27 16:20
 */
public interface LinkService extends SuperService<Link> {
    /**
     * 通过页大小获取友链列表
     *
     * @param pageSize
     * @return
     */
    List<Link> getListByPageSize(Integer pageSize);

    /**
     * 获取友链列表
     *
     * @param linkVO
     * @return
     */
    IPage<Link> getPageList(LinkVO linkVO);

    /**
     * 新增友链
     *
     * @param linkVO
     */
    String addLink(LinkVO linkVO);

    /**
     * 编辑友链
     *
     * @param linkVO
     */
    String editLink(LinkVO linkVO);

    /**
     * 删除友链
     *
     * @param linkVO
     */
    String deleteLink(LinkVO linkVO);

    /**
     * 置顶友链
     *
     * @param linkVO
     */
    String stickLink(LinkVO linkVO);

    /**
     * 点击友链
     *
     * @return
     */
    String addLinkCount(String uid);
}
