package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.holder.RequestHolder;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.Todo;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.mapper.TodoMapper;
import com.huanghongbe.zoom.xo.service.TodoService;
import com.huanghongbe.zoom.xo.vo.TodoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-12 2:45
 */
@Service
public class TodoServiceImpl extends SuperServiceImpl<TodoMapper, Todo> implements TodoService {
    @Autowired
    private TodoService todoService;
    @Override
    public void toggleAll(Integer done, String adminUid) {

    }

    @Override
    public IPage<Todo> getPageList(TodoVO todoVO) {
        String adminUid = RequestHolder.getAdminUid();
        QueryWrapper<Todo> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotEmpty(todoVO.getKeyword()) && !StringUtils.isEmpty(todoVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.TEXT, todoVO.getKeyword().trim());
        }
        queryWrapper.eq(SQLConf.ADMINUID, adminUid);
        //按时间顺序倒排
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        return todoService.page(new Page<>(todoVO.getCurrentPage(),todoVO.getPageSize()), queryWrapper);
    }

    @Override
    public String addTodo(TodoVO todoVO) {
        return null;
    }

    @Override
    public String editTodo(TodoVO todoVO) {
        return null;
    }

    @Override
    public String deleteTodo(TodoVO todoVO) {
        return null;
    }

    @Override
    public String editBatchTodo(TodoVO todoVO) {
        return null;
    }
}
