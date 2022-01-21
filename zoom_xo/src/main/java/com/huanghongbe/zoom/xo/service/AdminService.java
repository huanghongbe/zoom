package com.huanghongbe.zoom.xo.service;

import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.Admin;
import com.huanghongbe.zoom.xo.vo.AdminVO;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-21 23:17
 */
public interface AdminService extends SuperService<Admin> {
    /**
     * 通过UID获取Admin
     *
     * @param uid
     * @return
     */
    Admin getAdminByUid(String uid);

    /**
     * 获取在线用户列表
     *
     * @param adminVO
     * @return
     */
    String getOnlineAdminList(AdminVO adminVO);

    /**
     * Web端通过用户名获取一个Admin
     *
     * @param userName
     * @return
     */
    Admin getAdminByUser(String userName);

    /**
     * 获取当前管理员
     *
     * @return
     */
    Admin getMe();

    /**
     * 添加在线用户
     *
     * @param admin            管理员
     * @param expirationSecond 过期时间【秒】
     */
    void addOnlineAdmin(Admin admin, Long expirationSecond);

    /**
     * 获取管理员列表
     *
     * @param adminVO
     * @return
     */
    String getList(AdminVO adminVO);

    /**
     * 添加管理员
     *
     * @param adminVO
     * @return
     */
    String addAdmin(AdminVO adminVO);

    /**
     * 编辑管理员
     *
     * @param adminVO
     * @return
     */
    String editAdmin(AdminVO adminVO);

    /**
     * 编辑当前管理员信息
     *
     * @return
     */
    String editMe(AdminVO adminVO);

    /**
     * 修改密码
     *
     * @return
     */
    String changePwd(String oldPwd, String newPwd);

    /**
     * 重置密码
     *
     * @param adminVO
     * @return
     */
    String resetPwd(AdminVO adminVO);

    /**
     * 批量删除管理员
     *
     * @param adminUids
     * @return
     */
    String deleteBatchAdmin(List<String> adminUids);

    String forceLogout(List<String> tokenList);
}
