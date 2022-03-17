package com.huanghongbe.zoom.admin.restapi;

import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.validator.group.Delete;
import com.huanghongbe.zoom.base.validator.group.GetList;
import com.huanghongbe.zoom.base.validator.group.Insert;
import com.huanghongbe.zoom.base.validator.group.Update;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.CategoryMenuService;
import com.huanghongbe.zoom.xo.vo.CategoryMenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 菜单表 RestApi
 *
 */

@RestController
@RequestMapping("/categoryMenu")
@Slf4j
public class CategoryMenuRestApi {

    @Autowired
    CategoryMenuService categoryMenuService;

    @AuthorityVerify
    @RequestMapping(value = "/getList", method = RequestMethod.GET)
    public String getList(@Validated({GetList.class}) @RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(categoryMenuService.getPageList(categoryMenuVO));
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public String getAll(@RequestParam(value = "keyword", required = false) String keyword) {
        return ResultUtil.successWithData(categoryMenuService.getAllList(keyword));
    }

    @RequestMapping(value = "/getButtonAll", method = RequestMethod.GET)
    public String getButtonAll(@RequestParam(value = "keyword", required = false) String keyword) {

        return ResultUtil.successWithData(categoryMenuService.getButtonAllList(keyword));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加菜单")
    @PostMapping("/add")
    public String add(@Validated({Insert.class})@RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.addCategoryMenu(categoryMenuVO);
    }

    @AuthorityVerify
    @PostMapping("/edit")
    public String edit(@Validated({Update.class})@RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.editCategoryMenu(categoryMenuVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "删除菜单")
    @PostMapping("/delete")
    public String delete(@Validated({Delete.class}) @RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.deleteCategoryMenu(categoryMenuVO);
    }

    /**
     * 如果是一级菜单，直接置顶在最前面，二级菜单，就在一级菜单内置顶
     *
     */
    @AuthorityVerify
    @OperationLogger(value = "置顶菜单")
    @PostMapping("/stick")
    public String stick(@Validated({Delete.class}) @RequestBody CategoryMenuVO categoryMenuVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        return categoryMenuService.stickCategoryMenu(categoryMenuVO);
    }
}

