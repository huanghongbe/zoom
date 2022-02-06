package com.huanghongbe.zoom.xo.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Admin;
import com.huanghongbe.zoom.commons.feign.PictureFeignClient;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.AdminMapper;
import com.huanghongbe.zoom.xo.service.AdminService;
import com.huanghongbe.zoom.xo.utils.WebUtil;
import com.huanghongbe.zoom.xo.vo.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    @Autowired
    private PictureFeignClient pictureFeignClient;

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
