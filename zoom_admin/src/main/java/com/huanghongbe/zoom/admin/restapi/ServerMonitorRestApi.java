package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.enums.SysConf;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.ServerInfo.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务监控RestApi（CPU、内存、核心）
 *
 */

@RestController
@RequestMapping("/monitor")
@Slf4j
public class ServerMonitorRestApi {

    @AuthorityVerify
    @GetMapping("/getServerInfo")
    public String getInfo() {
        ServerInfo server = new ServerInfo();
        server.copyTo();
        return ResultUtil.result(SysConf.SUCCESS, server);
    }

}
