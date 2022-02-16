package com.huanghongbe.zoom.admin.restapi;
import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.admin.enums.MessageConf;
import com.huanghongbe.zoom.admin.enums.SysConf;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.service.SysDictDataService;
import com.huanghongbe.zoom.xo.vo.SysDictDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 字典数据 RestApi
 *
 */
@RestController
@RequestMapping("/sysDictData")
@Slf4j
public class SysDictDataRestApi {

    @Autowired
    private SysDictDataService sysDictDataService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@RequestBody SysDictDataVO sysDictDataVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取字典数据列表");
        return ResultUtil.successWithData(sysDictDataService.getPageList(sysDictDataVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加字典数据")
    @PostMapping("/add")
    public String add(HttpServletRequest request, @RequestBody SysDictDataVO sysDictDataVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictDataService.addSysDictData(sysDictDataVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑字典数据")
    @PostMapping("/edit")
    public String edit(HttpServletRequest request,@RequestBody SysDictDataVO sysDictDataVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictDataService.editSysDictData(sysDictDataVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除字典数据")
    @PostMapping("/deleteBatch")
    public String delete(HttpServletRequest request,@RequestBody List<SysDictDataVO> sysDictDataVoList, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return sysDictDataService.deleteBatchSysDictData(sysDictDataVoList);
    }

    @PostMapping("/getListByDictType")
    public String getListByDictType(@RequestParam("dictType") String dictType) {
        if (StringUtils.isEmpty(dictType)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }
        return ResultUtil.result(SysConf.SUCCESS, sysDictDataService.getListByDictType(dictType));
    }

    @PostMapping("/getListByDictTypeList")
    public String getListByDictTypeList(@RequestBody List<String> dictTypeList) {
        if (dictTypeList.size() <= 0) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.OPERATION_FAIL);
        }
        return ResultUtil.result(SysConf.SUCCESS, sysDictDataService.getListByDictTypeList(dictTypeList));
    }

}

