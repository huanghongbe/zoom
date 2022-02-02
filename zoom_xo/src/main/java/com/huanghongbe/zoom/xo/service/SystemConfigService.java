package com.huanghongbe.zoom.xo.service;

import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.SystemConfig;
import com.huanghongbe.zoom.xo.vo.SystemConfigVO;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-02 22:16
 */
public interface SystemConfigService extends SuperService<SystemConfig> {
    /**
     * 获取系统配置
     *
     * @return
     */
    SystemConfig getConfig();

    /**
     * 通过Key前缀清空Redis缓存
     *
     * @param key
     * @return
     */
    String cleanRedisByKey(List<String> key);

    /**
     * 修改系统配置
     *
     * @param systemConfigVO
     * @return
     */
    String editSystemConfig(SystemConfigVO systemConfigVO);

    /**
     * 获取系统配置中的搜索模式
     * @return
     */
    String getSearchModel();
}
