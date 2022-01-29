package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.AdminService;
import com.huanghongbe.zoom.xo.service.TagService;
import com.huanghongbe.zoom.xo.service.WebConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:01
 */
@RestController
@RequestMapping("/about")
@Slf4j
public class AboutMeRestApi {
    @Autowired
    private AdminService adminService;

    @Autowired
    private WebConfigService webConfigService;


    @GetMapping("/getMe")
    public String getMe() {

        log.info("获取关于我的信息");
        return ResultUtil.result(SysConf.SUCCESS, adminService.getAdminByUser(SysConf.ADMIN));
    }

    @GetMapping("/getContact")
    public String getContact() {
        log.info("获取联系方式");
        return ResultUtil.result(SysConf.SUCCESS, webConfigService.getWebConfigByShowList());
    }

}
