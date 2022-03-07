package com.huanghongbe.zoom.picture.config;

import com.huanghongbe.zoom.commons.entity.OnlineAdmin;
import com.huanghongbe.zoom.picture.enums.RedisConf;
import com.huanghongbe.zoom.picture.enums.SysConf;
import com.huanghongbe.zoom.utils.JsonUtils;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.SpringUtils;
import com.huanghongbe.zoom.utils.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token拦截器
 *
 */
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        StringBuffer requestURL = request.getRequestURL();
        //得到请求头信息authorization信息
        String authHeader = "";

        if (request.getHeader("Authorization") != null) {
            authHeader = request.getHeader("Authorization");
        } else if (request.getParameter(SysConf.TOKEN) != null) {
            authHeader = request.getParameter(SysConf.TOKEN);
        }

        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith("bearer_")) {
            // 获取在线的管理员信息
            RedisUtil redisUtil = SpringUtils.getBean(RedisUtil.class);
            String onlineAdmin = redisUtil.get(RedisConf.LOGIN_TOKEN_KEY + RedisConf.SEGMENTATION + authHeader);
            if (StringUtils.isNotEmpty(onlineAdmin)) {
                // 得到管理员UID和 Name
                OnlineAdmin admin = JsonUtils.jsonToPojo(onlineAdmin, OnlineAdmin.class);
                request.setAttribute(SysConf.ADMIN_UID, admin.getAdminUid());
                request.setAttribute(SysConf.NAME, admin.getUserName());
                request.setAttribute(SysConf.TOKEN, authHeader);
            }
        }
        return true;
    }

}

