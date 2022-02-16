package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.SystemConfigService;
import com.huanghongbe.zoom.xo.vo.SystemConfigVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-12 1:42
 */
@RestController
@RequestMapping("/systemConfig")
@Slf4j
public class SystemConfigRestApi {
    @Autowired
    private SystemConfigService systemConfigService;

    @AuthorityVerify
    @GetMapping("/getSystemConfig")
    public String getSystemConfig() {
        return ResultUtil.successWithData(systemConfigService.getConfig());
    }

    @AuthorityVerify
    @PostMapping("/cleanRedisByKey")
    public String cleanRedisByKey(@RequestBody List<String> key) {
        return systemConfigService.cleanRedisByKey(key);
    }

    @AuthorityVerify
    @OperationLogger(value = "修改系统配置")
    @PostMapping("/editSystemConfig")
    public String editSystemConfig(@RequestBody SystemConfigVO systemConfigVO) {
        return systemConfigService.editSystemConfig(systemConfigVO);
    }
}
