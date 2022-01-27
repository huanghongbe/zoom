package com.huanghongbe.zoom.xo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.exception.exceptionType.QueryException;
import com.huanghongbe.zoom.base.global.ErrorCode;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.SysParams;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.SysParamsMapper;
import com.huanghongbe.zoom.xo.service.SysParamsService;
import com.huanghongbe.zoom.xo.vo.SysParamsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-27 16:49
 */
@Service
public class SysParamsServiceImpl extends SuperServiceImpl<SysParamsMapper,SysParams> implements SysParamsService {
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public IPage<SysParams> getPageList(SysParamsVO sysParamsVO) {
        QueryWrapper<SysParams> queryWrapper = new QueryWrapper<>();
        // 参数名称
        if (StringUtils.isNotEmpty(sysParamsVO.getParamsName())) {
            queryWrapper.like(SQLConf.PARAMS_NAME, sysParamsVO.getParamsName().trim());
        }
        // 参数键名
        if (StringUtils.isNotEmpty(sysParamsVO.getParamsKey())) {
            queryWrapper.like(SQLConf.PARAMS_KEY, sysParamsVO.getParamsKey().trim());
        }
//        Page<SysParams> page = new Page<>();
//        page.setCurrent(sysParamsVO.getCurrentPage());
//        page.setSize(sysParamsVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT, SQLConf.CREATE_TIME);
        return sysParamsService.page(new Page<>(sysParamsVO.getCurrentPage(),sysParamsVO.getPageSize()), queryWrapper);
    }

    @Override
    public SysParams getSysParamsByKey(String paramsKey) {
        QueryWrapper<SysParams> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.PARAMS_KEY, paramsKey);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.last(SysConf.LIMIT_ONE);
        return sysParamsService.getOne(queryWrapper);
    }

    @Override
    public String getSysParamsValueByKey(String paramsKey) {
        // 判断Redis中是否包含该key的数据
        String redisKey = RedisConf.SYSTEM_PARAMS + RedisConf.SEGMENTATION + paramsKey;
        String paramsValue = redisUtil.get(redisKey);
        // 如果Redis中不存在，那么从数据库中获取
        if (StringUtils.isEmpty(paramsValue)) {
            SysParams sysParams = sysParamsService.getSysParamsByKey(paramsKey);
            // 如果数据库也不存在，将抛出异常【需要到找到 doc/数据库脚本 更新数据库中的 t_sys_params表】
            if (sysParams == null || StringUtils.isEmpty(sysParams.getParamsValue())) {
                throw new QueryException(ErrorCode.PLEASE_CONFIGURE_SYSTEM_PARAMS, MessageConf.PLEASE_CONFIGURE_SYSTEM_PARAMS);
            }
            paramsValue = sysParams.getParamsValue();
            redisUtil.set(redisKey, paramsValue);
        }
        return paramsValue;
    }

    @Override
    public String addSysParams(SysParamsVO sysParamsVO) {
        return null;
    }

    @Override
    public String editSysParams(SysParamsVO sysParamsVO) {
        return null;
    }

    @Override
    public String deleteBatchSysParams(List<SysParamsVO> sysParamsVOList) {
        return null;
    }
}
