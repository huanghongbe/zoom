package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.WebNavbar;
import com.huanghongbe.zoom.xo.vo.WebNavbarVO;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 1:09
 */
public interface WebNavbarService extends SuperService<WebNavbar> {

    /**
     * 分页获取门户导航栏
     *
     * @param webNavbarVO
     * @return
     */
    IPage<WebNavbar> getPageList(WebNavbarVO webNavbarVO);

    /**
     * 获取所有门户导航栏
     *
     * @return
     */
    List<WebNavbar> getAllList();

    /**
     * 新增门户导航栏
     *
     * @param webNavbarVO
     */
    String addWebNavbar(WebNavbarVO webNavbarVO);

    /**
     * 编辑门户导航栏
     *
     * @param webNavbarVO
     */
    String editWebNavbar(WebNavbarVO webNavbarVO);

    /**
     * 删除门户导航栏
     *
     * @param webNavbarVO
     */
    String deleteWebNavbar(WebNavbarVO webNavbarVO);

}
