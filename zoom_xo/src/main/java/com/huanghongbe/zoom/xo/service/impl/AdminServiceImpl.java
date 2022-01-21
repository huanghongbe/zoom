package com.huanghongbe.zoom.xo.service.impl;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Admin;
import com.huanghongbe.zoom.xo.mapper.AdminMapper;
import com.huanghongbe.zoom.xo.service.AdminService;
import com.huanghongbe.zoom.xo.vo.AdminVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-21 23:18
 */
@Service
public class AdminServiceImpl extends SuperServiceImpl<AdminMapper, Admin> implements AdminService {

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
        return null;
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
