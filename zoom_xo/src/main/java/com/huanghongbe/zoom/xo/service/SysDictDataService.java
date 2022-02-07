package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.SysDictData;
import com.huanghongbe.zoom.xo.vo.SysDictDataVO;

import java.util.List;
import java.util.Map;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-07 16:54
 */
public interface SysDictDataService extends SuperService<SysDictData> {

    /**
     * 获取数据字典列表
     *
     * @param sysDictDataVO
     * @return
     */
    IPage<SysDictData> getPageList(SysDictDataVO sysDictDataVO);

    /**
     * 新增数据字典
     *
     * @param sysDictDataVO
     */
    String addSysDictData(SysDictDataVO sysDictDataVO);

    /**
     * 编辑数据字典
     *
     * @param sysDictDataVO
     */
    String editSysDictData(SysDictDataVO sysDictDataVO);

    /**
     * 批量删除数据字典
     *
     * @param sysDictDataVOList
     */
    String deleteBatchSysDictData(List<SysDictDataVO> sysDictDataVOList);

    /**
     * 根据字典类型获取字典数据
     *
     * @param dictType
     * @return
     */
    Map<String, Object> getListByDictType(String dictType);

    /**
     * 根据字典类型数组获取字典数据
     *
     * @param dictTypeList
     * @return
     */
    Map<String, Map<String, Object>> getListByDictTypeList(List<String> dictTypeList);
}
