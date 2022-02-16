package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.TodoService;
import com.huanghongbe.zoom.xo.vo.TodoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-12 2:33
 */
@RestController
@RequestMapping("/todo")
@Slf4j
public class TodoRestApi {

    @Autowired
    private TodoService todoService;
    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(HttpServletRequest request, @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("执行获取代办事项列表");
        return ResultUtil.result(SysConf.SUCCESS, todoService.getPageList(todoVO));
    }

    @AuthorityVerify
    @OperationLogger(value = "增加代办事项")
    @PostMapping("/add")
    public String add(HttpServletRequest request, @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return todoService.addTodo(todoVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑代办事项")
    @PostMapping("/edit")
    public String edit(HttpServletRequest request, @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return todoService.editTodo(todoVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除代办事项")
    @PostMapping("/delete")
    public String delete(HttpServletRequest request, @RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return todoService.deleteTodo(todoVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量编辑代办事项")
    @PostMapping("/toggleAll")
    public String toggleAll(HttpServletRequest request,@RequestBody TodoVO todoVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return todoService.editBatchTodo(todoVO);
    }
}
