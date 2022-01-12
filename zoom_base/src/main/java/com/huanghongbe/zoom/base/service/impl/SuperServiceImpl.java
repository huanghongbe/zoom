package com.huanghongbe.zoom.base.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huanghongbe.zoom.base.mapper.SuperMapper;
import com.huanghongbe.zoom.base.service.SuperService;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-13 0:21
 */
public class SuperServiceImpl<M extends SuperMapper<T>,T> extends ServiceImpl<M,T> implements SuperService<T> {
}