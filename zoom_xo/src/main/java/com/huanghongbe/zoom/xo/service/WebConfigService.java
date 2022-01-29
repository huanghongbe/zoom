package com.huanghongbe.zoom.xo.service;

import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.WebConfig;
import com.huanghongbe.zoom.xo.vo.WebConfigVO;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:04
 */
public interface WebConfigService extends SuperService<WebConfig> {
    /**
     * 获取网站配置
     *
     * @return
     */
    WebConfig getWebConfig();

    /**
     * 获取网站名称
     * @return
     */
    String getWebSiteName();

    /**
     * 通过显示列表获取配置
     *
     * @return
     */
    WebConfig getWebConfigByShowList();

    /**
     * 修改网站配置
     *
     * @param webConfigVO
     * @return
     */
    String editWebConfig(WebConfigVO webConfigVO);

    /**
     * 是否开启该登录方式【账号密码、码云、Github、QQ、微信】
     * @param loginType
     * @return
     */
    Boolean isOpenLoginType(String loginType);
}
