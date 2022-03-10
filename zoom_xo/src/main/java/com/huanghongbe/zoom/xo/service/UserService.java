package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.User;
import com.huanghongbe.zoom.xo.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-07 23:21
 */
public interface UserService extends SuperService<User> {

    /**
     * 记录用户信息
     *
     * @param response
     */
    User insertUserInfo(HttpServletRequest request, String response);

    /**
     * 通过source uuid获取用户类
     *
     * @param source
     * @param uuid
     * @return
     */
    User getUserBySourceAnduuid(String source, String uuid);

    /**
     * 获取用户数
     *
     * @param status
     * @return
     */
    Integer getUserCount(int status);

    /**
     * 设置Request相关，如浏览器，IP，IP来源
     *
     * @param user
     * @return
     */
    User setRequestInfo(User user);

    /**
     * 通过ids获取用户列表
     *
     * @param ids
     * @return
     */
    List<User> getUserListByIds(List<String> ids);

    /**
     * 获取用户列表
     *
     * @param userVO
     * @return
     */
    IPage<User> getPageList(UserVO userVO);

    /**
     * 新增用户
     *
     * @param userVO
     */
    String addUser(UserVO userVO);

    /**
     * 编辑用户
     *
     * @param userVO
     */
    String editUser(UserVO userVO);

    /**
     * 删除用户
     *
     * @param userVO
     */
    String deleteUser(UserVO userVO);

    /**
     * 重置用户密码
     *
     * @param userVO
     * @return
     */
    String resetUserPassword(UserVO userVO);
}
