package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.validator.group.GetList;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.SubjectItemService;
import com.huanghongbe.zoom.xo.service.SubjectService;
import com.huanghongbe.zoom.xo.vo.SubjectItemVO;
import com.huanghongbe.zoom.xo.vo.SubjectVO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:47
 */
@RestController
@RequestMapping("/subject")
@Slf4j
public class SubjectRestApi {
    @Autowired
    SubjectService subjectService;

    @Autowired
    SubjectItemService subjectItemService;

    @PostMapping("/getList")
    public String getList(@Validated({GetList.class})@RequestBody SubjectVO subjectVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, subjectService.getPageList(subjectVO));
    }

    @PostMapping("/getItemList")
    public String getItemList(@Validated({GetList.class})@RequestBody SubjectItemVO subjectItemVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.result(SysConf.SUCCESS, subjectItemService.getPageList(subjectItemVO));
    }
}
