package com.huanghongbe.zoom.xo.service.impl;

import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.User;
import com.huanghongbe.zoom.xo.mapper.UserMapper;
import com.huanghongbe.zoom.xo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-07 23:22
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {
}
