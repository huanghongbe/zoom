package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanghongbe.zoom.base.enums.EAccountType;
import com.huanghongbe.zoom.base.exception.exceptionType.QueryException;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.base.global.ErrorCode;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.WebConfig;
import com.huanghongbe.zoom.commons.feign.PictureFeignClient;
import com.huanghongbe.zoom.utils.JsonUtils;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.mapper.WebConfigMapper;
import com.huanghongbe.zoom.xo.service.WebConfigService;
import com.huanghongbe.zoom.xo.utils.WebUtil;
import com.huanghongbe.zoom.xo.vo.WebConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 4:05
 */
@Service
public class WebConfigServiceImpl extends SuperServiceImpl<WebConfigMapper, WebConfig> implements WebConfigService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private PictureFeignClient pictureFeignClient;
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
        //从Redis中获取IP来源
        String webConfigResult = redisUtil.get(RedisConf.WEB_CONFIG);
        if (StringUtils.isNotEmpty(webConfigResult)) {
            WebConfig webConfig = JsonUtils.jsonToPojo(webConfigResult, WebConfig.class);
            return webConfig;
        }

        QueryWrapper<WebConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        WebConfig webConfig = webConfigService.getOne(queryWrapper);
        if (webConfig == null) {
            throw new QueryException(ErrorCode.SYSTEM_CONFIG_IS_NOT_EXIST, MessageConf.SYSTEM_CONFIG_IS_NOT_EXIST);
        }
        StringBuilder stringBuilder = new StringBuilder();
        String pictureResult = "";

        // 获取LOGO
        if (StringUtils.isNotEmpty(webConfig.getLogo())) {
            stringBuilder.append(webConfig.getLogo() + Constants.SYMBOL_COMMA);
        }
        if (StringUtils.isNotEmpty(webConfig.getAliPay())) {
            stringBuilder.append(webConfig.getAliPay() + Constants.SYMBOL_COMMA);
        }
        if (StringUtils.isNotEmpty(webConfig.getWeixinPay())) {
            stringBuilder.append(webConfig.getWeixinPay() + Constants.SYMBOL_COMMA);
        }
        if (stringBuilder != null) {
            pictureResult = this.pictureFeignClient.getPicture(stringBuilder.toString(), Constants.SYMBOL_COMMA);
        }
        List<Map<String, Object>> pictureList = webUtil.getPictureMap(pictureResult);
        Map<String, String> pictureMap = new HashMap<>();
        pictureList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });

        // 获取LOGO
        if (StringUtils.isNotEmpty(webConfig.getLogo()) && pictureMap.get(webConfig.getLogo()) != null) {
            webConfig.setLogoPhoto(pictureMap.get(webConfig.getLogo()));
        }
        // 获取阿里支付码
        if (StringUtils.isNotEmpty(webConfig.getAliPay()) && pictureMap.get(webConfig.getAliPay()) != null) {
            webConfig.setAliPayPhoto(pictureMap.get(webConfig.getAliPay()));
        }
        // 获取微信支付码
        if (StringUtils.isNotEmpty(webConfig.getWeixinPay()) && pictureMap.get(webConfig.getWeixinPay()) != null) {
            webConfig.setWeixinPayPhoto(pictureMap.get(webConfig.getWeixinPay()));
        }

        // 过滤一些不需要显示的用户账号信息
        String showListJson = webConfig.getShowList();

        // 获取联系方式
        String email = webConfig.getEmail();
        String qqNumber = webConfig.getQqNumber();
        String qqGroup = webConfig.getQqGroup();
        String github = webConfig.getGithub();
        String gitee = webConfig.getGitee();
        String weChat = webConfig.getWeChat();

        // 将联系方式全部置空
        webConfig.setEmail("");
        webConfig.setQqNumber("");
        webConfig.setQqGroup("");
        webConfig.setGithub("");
        webConfig.setGitee("");
        webConfig.setWeChat("");

        // 判断哪些联系方式需要显示出来
        List<String> showList = JsonUtils.jsonToList(showListJson, String.class);
        showList.forEach(item->{
            if (EAccountType.EMail.getCode().equals(item)) {
                webConfig.setEmail(email);
            }
            if (EAccountType.QQNumber.getCode().equals(item)) {
                webConfig.setQqNumber(qqNumber);
            }
            if (EAccountType.QQGroup.getCode().equals(item)) {
                webConfig.setQqGroup(qqGroup);
            }
            if (EAccountType.Github.getCode().equals(item)) {
                webConfig.setGithub(github);
            }
            if (EAccountType.Gitee.getCode().equals(item)) {
                webConfig.setGitee(gitee);
            }
            if (EAccountType.WeChat.getCode().equals(item)) {
                webConfig.setWeChat(weChat);
            }
        });
        // 将WebConfig存到Redis中 [过期时间24小时]
        redisUtil.setEx(RedisConf.WEB_CONFIG, JsonUtils.objectToJson(webConfig), 24, TimeUnit.HOURS);
        return webConfig;
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