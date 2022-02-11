package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.global.BaseSQLConf;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.User;
import com.huanghongbe.zoom.xo.mapper.UserMapper;
import com.huanghongbe.zoom.xo.service.UserService;
import com.huanghongbe.zoom.xo.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-07 23:22
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserService userService;
    @Override
    public User insertUserInfo(HttpServletRequest request, String response) {
        return null;
    }

    @Override
    public User getUserBySourceAnduuid(String source, String uuid) {
        return null;
    }

    @Override
    public Integer getUserCount(int status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.STATUS, status);
        return userService.count(queryWrapper);
    }

    @Override
    public User serRequestInfo(User user) {
        return null;
    }

    @Override
    public List<User> getUserListByIds(List<String> ids) {
        return null;
    }

    @Override
    public IPage<User> getPageList(UserVO userVO) {
        return null;
    }

    @Override
    public String addUser(UserVO userVO) {
        return null;
    }

    @Override
    public String editUser(UserVO userVO) {
        return null;
    }

    @Override
    public String deleteUser(UserVO userVO) {
        return null;
    }

    @Override
    public String resetUserPassword(UserVO userVO) {
        return null;
    }
}
