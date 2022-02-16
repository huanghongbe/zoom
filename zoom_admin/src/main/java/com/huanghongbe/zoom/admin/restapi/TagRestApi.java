package com.huanghongbe.zoom.admin.restapi;


import com.huanghongbe.zoom.admin.annotion.AuthorityVerify.AuthorityVerify;
import com.huanghongbe.zoom.admin.annotion.AvoidRepeatableCommit.AvoidRepeatableCommit;
import com.huanghongbe.zoom.admin.annotion.OperationLogger.OperationLogger;
import com.huanghongbe.zoom.admin.enums.SysConf;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.xo.service.TagService;
import com.huanghongbe.zoom.xo.vo.TagVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 标签表 RestApi
 *
 */
@RestController
@RequestMapping("/tag")
@Slf4j
public class TagRestApi {

    @Autowired
    private TagService tagService;

    @AuthorityVerify
    @PostMapping("/getList")
    public String getList(@RequestBody TagVO tagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, tagService.getPageList(tagVO));
    }

    @AvoidRepeatableCommit
    @AuthorityVerify
    @OperationLogger(value = "增加标签")
    @PostMapping("/add")
    public String add(@RequestBody TagVO tagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("增加标签");
        return tagService.addTag(tagVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "编辑标签")
    @PostMapping("/edit")
    public String edit(@RequestBody TagVO tagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("编辑标签");
        return tagService.editTag(tagVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "批量删除标签")
    @PostMapping("/deleteBatch")
    public String delete(@RequestBody List<TagVO> tagVoList, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除标签");
        return tagService.deleteBatchTag(tagVoList);
    }

    @AuthorityVerify
    @OperationLogger(value = "置顶标签")
    @PostMapping("/stick")
    public String stick(@RequestBody TagVO tagVO, BindingResult result) {
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("置顶标签");
        return tagService.stickTag(tagVO);
    }

    @AuthorityVerify
    @OperationLogger(value = "通过点击量排序标签")
    @PostMapping("/tagSortByClickCount")
    public String tagSortByClickCount() {
        log.info("通过点击量排序标签");
        return tagService.tagSortByClickCount();
    }

    /**
     * 通过引用量排序标签
     * 引用量就是所有的文章中，有多少使用了该标签，如果使用的越多，该标签的引用量越大，那么排名越靠前
     *
     * @return
     */
    @AuthorityVerify
    @OperationLogger(value = "通过引用量排序标签")
    @PostMapping("/tagSortByCite")
    public String tagSortByCite() {
        log.info("通过引用量排序标签");
        return tagService.tagSortByCite();
    }
}

