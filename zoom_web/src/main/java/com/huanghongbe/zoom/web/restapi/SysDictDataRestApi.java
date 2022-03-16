package com.huanghongbe.zoom.web.restapi;

import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.SysDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据查询 RestApi
 *
 */
@RestController
@RequestMapping("/sysDictData")
@Slf4j
public class SysDictDataRestApi {

    @Autowired
    SysDictDataService sysDictDataService;

    @PostMapping("/getListByDictType")
    public String getListByDictType(@RequestParam("dictType") String dictType) {

        log.info("根据字典类型获取字典数据");
        return ResultUtil.result(SysConf.SUCCESS, sysDictDataService.getListByDictType(dictType));
    }

    @PostMapping("/getListByDictTypeList")
    public String getListByDictTypeList(@RequestBody List<String> dictTypeList) {
        log.info("根据字典类型数组获取字典数据");
        return ResultUtil.result(SysConf.SUCCESS, sysDictDataService.getListByDictTypeList(dictTypeList));
    }
}

