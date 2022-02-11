package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.Todo;
import com.huanghongbe.zoom.xo.vo.TodoVO;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-12 2:44
 */
public interface TodoService extends SuperService<Todo> {

    /**
     * 批量更新代办事项的状态
     *
     * @param done     : 状态
     * @param adminUid : 管理员UID
     */
    void toggleAll(Integer done, String adminUid);

    /**
     * 获取待办事项列表
     *
     * @param todoVO
     * @return
     */
    IPage<Todo> getPageList(TodoVO todoVO);

    /**
     * 新增待办事项
     *
     * @param todoVO
     */
    String addTodo(TodoVO todoVO);

    /**
     * 编辑待办事项
     *
     * @param todoVO
     */
    public String editTodo(TodoVO todoVO);

    /**
     * 删除待办事项
     *
     * @param todoVO
     */
    String deleteTodo(TodoVO todoVO);

    /**
     * 批量编辑待办事项
     *
     * @param todoVO
     */
    String editBatchTodo(TodoVO todoVO);
}
