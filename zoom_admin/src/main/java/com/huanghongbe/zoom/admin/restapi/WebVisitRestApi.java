package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.enums.SysConf;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.validator.group.GetList;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.WebVisitService;
import com.huanghongbe.zoom.xo.vo.WebVisitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户访问表 RestApi
 *
 */
@RestController
@RequestMapping("/webVisit")
@Slf4j
public class WebVisitRestApi {

    @Autowired
    private WebVisitService webVisitService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class})@RequestBody WebVisitVO webVisitVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, webVisitService.getPageList(webVisitVO));
    }
}

