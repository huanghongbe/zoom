package com.huanghongbe.zoom.xo.service.impl;

import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.WebConfig;
import com.huanghongbe.zoom.xo.mapper.WebConfigMapper;
import com.huanghongbe.zoom.xo.service.WebConfigService;
import com.huanghongbe.zoom.xo.vo.WebConfigVO;
import org.springframework.stereotype.Service;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:05
 */
@Service
public class WebConfigServiceImpl extends SuperServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {
    @Override
    public WebConfig getWebConfig() {
        return null;
    }

    @Override
    public String getWebSiteName() {
        return null;
    }

    @Override
    public WebConfig getWebConfigByShowList() {
        return null;
    }

    @Override
    public String editWebConfig(WebConfigVO webConfigVO) {
        return null;
    }

    @Override
    public Boolean isOpenLoginType(String loginType) {
        return null;
    }
}
