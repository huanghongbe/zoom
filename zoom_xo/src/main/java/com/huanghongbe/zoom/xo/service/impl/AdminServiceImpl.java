package com.huanghongbe.zoom.xo.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.base.holder.RequestHolder;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Admin;
import com.huanghongbe.zoom.commons.entity.OnlineAdmin;
import com.huanghongbe.zoom.commons.feign.PictureFeignClient;
import com.huanghongbe.zoom.utils.*;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.AdminMapper;
import com.huanghongbe.zoom.xo.service.AdminService;
import com.huanghongbe.zoom.xo.utils.WebUtil;
import com.huanghongbe.zoom.xo.vo.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-21 23:18
 */
@Service
public class AdminServiceImpl extends SuperServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private WebUtil webUtil;
    @Autowired
    private AdminService adminService;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public Admin getAdminByUid(String uid) {
        return null;
    }

    @Override
    public String getOnlineAdminList(AdminVO adminVO) {
        return null;
    }

    @Override
    public Admin getAdminByUser(String userName) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.USER_NAME, userName);
        queryWrapper.last(SysConf.LIMIT_ONE);
        //清空密码，防止泄露
        Admin admin = adminService.getOne(queryWrapper);
        admin.setPassWord(null);
        //获取图片
        if (StringUtils.isNotEmpty(admin.getAvatar())) {
            String pictureList = this.pictureFeignClient.getPicture(admin.getAvatar(), Constants.SYMBOL_COMMA);
            admin.setPhotoList(webUtil.getPicture(pictureList));
        }
        Admin result = new Admin();
        result.setNickName(admin.getNickName());
        result.setOccupation(admin.getOccupation());
        result.setSummary(admin.getSummary());
        result.setAvatar(admin.getAvatar());
        result.setPhotoList(admin.getPhotoList());
        result.setPersonResume(admin.getPersonResume());
        return result;
    }

    @Override
    public Admin getMe() {
        return null;
    }

    @Override
    public void addOnlineAdmin(Admin admin, Long expirationSecond) {
        HttpServletRequest request = RequestHolder.getRequest();
        Map<String, String> map = IpUtils.getOsAndBrowserInfo(request);
        String os = map.get(SysConf.OS);
        String browser = map.get(SysConf.BROWSER);
        String ip = IpUtils.getIpAddr(request);
        OnlineAdmin onlineAdmin = new OnlineAdmin();
        onlineAdmin.setAdminUid(admin.getUid());
        onlineAdmin.setTokenId(admin.getTokenUid());
        onlineAdmin.setToken(admin.getValidCode());
        onlineAdmin.setOs(os);
        onlineAdmin.setBrowser(browser);
        onlineAdmin.setIpaddr(ip);
        onlineAdmin.setLoginTime(DateUtils.getNowTime());
        onlineAdmin.setRoleName(admin.getRole().getRoleName());
        onlineAdmin.setUserName(admin.getUserName());
        onlineAdmin.setExpireTime(DateUtils.getDateStr(new Date(), expirationSecond));
        //从Redis中获取IP来源
        String jsonResult = redisUtil.get(RedisConf.IP_SOURCE + Constants.SYMBOL_COLON + ip);
        if (StringUtils.isEmpty(jsonResult)) {
            String addresses = IpUtils.getAddresses(SysConf.IP + SysConf.EQUAL_TO + ip, SysConf.UTF_8);
            if (StringUtils.isNotEmpty(addresses)) {
                onlineAdmin.setLoginLocation(addresses);
                redisUtil.setEx(RedisConf.IP_SOURCE + Constants.SYMBOL_COLON + ip, addresses, 24, TimeUnit.HOURS);
            }
        } else {
            onlineAdmin.setLoginLocation(jsonResult);
        }
        // 将登录的管理员存储到在线用户表
        redisUtil.setEx(RedisConf.LOGIN_TOKEN_KEY + RedisConf.SEGMENTATION + admin.getValidCode(), JsonUtils.objectToJson(onlineAdmin), expirationSecond, TimeUnit.SECONDS);
        // 在维护一张表，用于 uuid - token 互相转换
        redisUtil.setEx(RedisConf.LOGIN_UUID_KEY + RedisConf.SEGMENTATION + admin.getTokenUid(), admin.getValidCode(), expirationSecond, TimeUnit.SECONDS);
    }

    @Override
    public String getList(AdminVO adminVO) {
        return null;
    }

    @Override
    public String addAdmin(AdminVO adminVO) {
        return null;
    }

    @Override
    public String editAdmin(AdminVO adminVO) {
        return null;
    }

    @Override
    public String editMe(AdminVO adminVO) {
        return null;
    }

    @Override
    public String changePwd(String oldPwd, String newPwd) {
        return null;
    }

    @Override
    public String resetPwd(AdminVO adminVO) {
        return null;
    }

    @Override
    public String deleteBatchAdmin(List<String> adminUids) {
        return null;
    }

    @Override
    public String forceLogout(List<String> tokenList) {
        return null;
    }
}
