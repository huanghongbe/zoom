package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.Role;
import com.huanghongbe.zoom.xo.vo.RoleVO;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-21 23:08
 */
public interface RoleService extends SuperService<Role>{
    /**
     * 获取角色列表
     *
     * @param roleVO
     * @return
     */
    IPage<Role> getPageList(RoleVO roleVO);

    /**
     * 新增角色
     *
     * @param roleVO
     */
    String addRole(RoleVO roleVO);

    /**
     * 编辑角色
     *
     * @param roleVO
     */
    String editRole(RoleVO roleVO);

    /**
     * 删除角色
     *
     * @param roleVO
     */
    String deleteRole(RoleVO roleVO);
}