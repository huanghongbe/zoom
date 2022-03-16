package com.huanghongbe.zoom.web.restapi;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.*;
import com.huanghongbe.zoom.base.exception.ThrowableUtils;
import com.huanghongbe.zoom.base.global.BaseSysConf;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.base.holder.RequestHolder;
import com.huanghongbe.zoom.commons.entity.*;
import com.huanghongbe.zoom.commons.feign.PictureFeignClient;
import com.huanghongbe.zoom.utils.FileUtils;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.web.annotion.log.BussinessLog;
import com.huanghongbe.zoom.web.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.service.*;
import com.huanghongbe.zoom.xo.utils.RabbitMqUtil;
import com.huanghongbe.zoom.xo.utils.WebUtil;
import com.huanghongbe.zoom.xo.vo.CommentVO;
import com.huanghongbe.zoom.xo.vo.UserVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 评论RestApi
 *
 */
@RestController
@RefreshScope
@RequestMapping("/web/comment")
@Slf4j
public class CommentRestApi {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private WebUtil webUtil;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RabbitMqUtil rabbitMqUtil;
    @Autowired
    private BlogService blogService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @Resource
    private PictureFeignClient pictureFeignClient;
//    @Autowired
//    private CommentReportService commentReportService;
    @Value(value = "${BLOG.USER_TOKEN_SURVIVAL_TIME}")
    private Long userTokenSurvivalTime;
    @Value(value = "${data.website.url}")
    private String dataWebsiteUrl;

    @GetMapping("/getUserReceiveCommentCount")
    public String getUserReceiveCommentCount(HttpServletRequest request) {
        log.info("获取用户收到的评论回复数");
        // 判断用户是否登录
        Integer commentCount = 0;
        if (request.getAttribute(SysConf.USER_UID) != null) {
            String userUid = request.getAttribute(SysConf.USER_UID).toString();
            String redisKey = RedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + userUid;
            String count = redisUtil.get(redisKey);
            if (StringUtils.isNotEmpty(count)) {
                commentCount = Integer.valueOf(count);
            }
        }
        return ResultUtil.successWithData(commentCount);
    }

    /**
     * 获取评论列表
     *
     * @param commentVO
     * @param result
     * @return
     */
    @PostMapping("/getList")
    public String getList(@RequestBody CommentVO commentVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
            queryWrapper.like(SQLConf.BLOG_UID, commentVO.getBlogUid());
        }
        queryWrapper.eq(SQLConf.SOURCE, commentVO.getSource());

        //分页
        Page<Comment> page = new Page<>();
        page.setCurrent(commentVO.getCurrentPage());
        page.setSize(commentVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.isNull(SQLConf.TO_UID);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.eq(SQLConf.TYPE, ECommentType.COMMENT);
        // 查询出所有的一级评论，进行分页显示
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();
        List<String> firstUidList = new ArrayList<>();
        list.forEach(item -> {
            firstUidList.add(item.getUid());
        });

        if (firstUidList.size() > 0) {
            // 查询一级评论下的子评论
            QueryWrapper<Comment> notFirstQueryWrapper = new QueryWrapper<>();
            notFirstQueryWrapper.in(SQLConf.FIRST_COMMENT_UID, firstUidList);
            notFirstQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
            List<Comment> notFirstList = commentService.list(notFirstQueryWrapper);
            // 将子评论加入总的评论中
            if (notFirstList.size() > 0) {
                list.addAll(notFirstList);
            }
        }

        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            String userUid = item.getUserUid();
            String toUserUid = item.getToUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                userUidList.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(toUserUid)) {
                userUidList.add(item.getToUserUid());
            }
        });
        Collection<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.listByIds(userUidList);
        }

        // 过滤掉用户的敏感信息
        List<User> filterUserList = new ArrayList<>();
        userList.forEach(item -> {
            User user = new User();
            user.setAvatar(item.getAvatar());
            user.setUid(item.getUid());
            user.setNickName(item.getNickName());
            user.setUserTag(item.getUserTag());
            filterUserList.add(user);
        });

        // 获取用户头像
        StringBuffer fileUids = new StringBuffer();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUids.append(item.getAvatar() + SysConf.FILE_SEGMENTATION);
            }
        });
        String pictureList = null;
        if (fileUids != null) {
            pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        Map<String, String> pictureMap = new HashMap<>();
        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });

        Map<String, User> userMap = new HashMap<>();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && pictureMap.get(item.getAvatar()) != null) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });

        Map<String, Comment> commentMap = new HashMap<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }
            commentMap.put(item.getUid(), item);
        });

        // 设置一级评论下的子评论
        Map<String, List<Comment>> toCommentListMap = new HashMap<>();
        for (int a = 0; a < list.size(); a++) {
            List<Comment> tempList = new ArrayList<>();
            for (int b = 0; b < list.size(); b++) {
                if (list.get(a).getUid().equals(list.get(b).getToUid())) {
                    tempList.add(list.get(b));
                }
            }
            toCommentListMap.put(list.get(a).getUid(), tempList);
        }
        List<Comment> firstComment = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isEmpty(item.getToUid())) {
                firstComment.add(item);
            }
        });
        pageList.setRecords(getCommentReplys(firstComment, toCommentListMap));
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }


    @PostMapping("/getListByUser")
    public String getListByUser(HttpServletRequest request,@RequestBody UserVO userVO) {

        if (request.getAttribute(SysConf.USER_UID) == null) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INVALID_TOKEN);
        }
        String requestUserUid = request.getAttribute(SysConf.USER_UID).toString();
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        //分页
        Page<Comment> page = new Page<>();
        page.setCurrent(userVO.getCurrentPage());
        page.setSize(userVO.getPageSize());
        queryWrapper.eq(SQLConf.TYPE, ECommentType.COMMENT);
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        // 查找出 我的评论 和 我的回复
        queryWrapper.and(wrapper -> wrapper.eq(SQLConf.USER_UID, requestUserUid).or().eq(SQLConf.TO_USER_UID, requestUserUid));
        IPage<Comment> pageList = commentService.page(page, queryWrapper);
        List<Comment> list = pageList.getRecords();
        List<String> userUidList = new ArrayList<>();
        list.forEach(item -> {
            String userUid = item.getUserUid();
            String toUserUid = item.getToUserUid();
            if (StringUtils.isNotEmpty(userUid)) {
                userUidList.add(item.getUserUid());
            }
            if (StringUtils.isNotEmpty(toUserUid)) {
                userUidList.add(item.getToUserUid());
            }
        });

        // 获取用户列表
        Collection<User> userList = new ArrayList<>();
        if (userUidList.size() > 0) {
            userList = userService.listByIds(userUidList);
        }
        // 过滤掉用户的敏感信息
        List<User> filterUserList = new ArrayList<>();
        userList.forEach(item -> {
            User user = new User();
            user.setAvatar(item.getAvatar());
            user.setUid(item.getUid());
            user.setNickName(item.getNickName());
            filterUserList.add(user);
        });
        // 获取用户头像
        StringBuffer fileUids = new StringBuffer();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUids.append(item.getAvatar() + SysConf.FILE_SEGMENTATION);
            }
        });
        String pictureList = null;
        if (fileUids != null) {
            pictureList = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureList);
        Map<String, String> pictureMap = new HashMap<>();
        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });

        Map<String, User> userMap = new HashMap<>();
        filterUserList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar()) && pictureMap.get(item.getAvatar()) != null) {
                item.setPhotoUrl(pictureMap.get(item.getAvatar()));
            }
            userMap.put(item.getUid(), item);
        });

        // 将评论列表划分为 我的评论 和 我的回复
        List<Comment> commentList = new ArrayList<>();
        List<Comment> replyList = new ArrayList<>();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }

            if (StringUtils.isNotEmpty(item.getToUserUid())) {
                item.setToUser(userMap.get(item.getToUserUid()));
            }
            // 设置sourceName
            if (StringUtils.isNotEmpty(item.getSource())) {
                try {
                    item.setSourceName(ECommentSource.valueOf(item.getSource()).getName());
                } catch (Exception e) {
                    log.error("ECommentSource转换异常");
                }
            }
            if (requestUserUid.equals(item.getUserUid())) {
                commentList.add(item);
            }
            if (requestUserUid.equals(item.getToUserUid())) {
                replyList.add(item);
            }
        });

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(SysConf.COMMENT_LIST, commentList);
        resultMap.put(SysConf.REPLY_LIST, replyList);
        return ResultUtil.result(SysConf.SUCCESS, resultMap);
    }

    /**
     * 获取用户点赞信息
     *
     * @return
     */
    @PostMapping("/getPraiseListByUser")
    public String getPraiseListByUser(@ApiParam(name = "currentPage", value = "当前页数", required = false) @RequestParam(name = "currentPage", required = false, defaultValue = "1") Long currentPage,
                                      @ApiParam(name = "pageSize", value = "每页显示数目", required = false) @RequestParam(name = "pageSize", required = false, defaultValue = "10") Long pageSize) {
        HttpServletRequest request = RequestHolder.getRequest();
        if (request.getAttribute(SysConf.USER_UID) == null || request.getAttribute(SysConf.TOKEN) == null) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INVALID_TOKEN);
        }
        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        QueryWrapper<Comment> queryWrappe = new QueryWrapper<>();
        queryWrappe.eq(SQLConf.USER_UID, userUid);
        queryWrappe.eq(SQLConf.TYPE, ECommentType.PRAISE);
        queryWrappe.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrappe.orderByDesc(SQLConf.CREATE_TIME);
        Page<Comment> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        IPage<Comment> pageList = commentService.page(page, queryWrappe);
        List<Comment> praiseList = pageList.getRecords();
        List<String> blogUids = new ArrayList<>();
        praiseList.forEach(item -> {
            blogUids.add(item.getBlogUid());
        });
        Map<String, Blog> blogMap = new HashMap<>();
        if (blogUids.size() > 0) {
            Collection<Blog> blogList = blogService.listByIds(blogUids);
            blogList.forEach(blog -> {
                // 并不需要content内容
                blog.setContent("");
                blogMap.put(blog.getUid(), blog);
            });
        }

        praiseList.forEach(item -> {
            if (blogMap.get(item.getBlogUid()) != null) {
                item.setBlog(blogMap.get(item.getBlogUid()));
            }
        });
        pageList.setRecords(praiseList);
        return ResultUtil.result(SysConf.SUCCESS, pageList);
    }

    @BussinessLog(value = "发表评论", behavior = EBehavior.PUBLISH_COMMENT)
    @PostMapping("/add")
    public String add( @RequestBody CommentVO commentVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        HttpServletRequest request = RequestHolder.getRequest();
        if (request.getAttribute(SysConf.USER_UID) == null) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INVALID_TOKEN);
        }
        QueryWrapper<WebConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConf.STATUS, EStatus.ENABLE);
        WebConfig webConfig = webConfigService.getOne(queryWrapper);
        // 判断是否开启全局评论功能
        if (SysConf.CAN_NOT_COMMENT.equals(webConfig.getOpenComment())) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.NO_COMMENTS_OPEN);
        }
        // 判断该博客是否开启评论功能
        if (StringUtils.isNotEmpty(commentVO.getBlogUid())) {
            Blog blog = blogService.getById(commentVO.getBlogUid());
            if (SysConf.CAN_NOT_COMMENT.equals(blog.getOpenComment())) {
                return ResultUtil.result(SysConf.ERROR, MessageConf.BLOG_NO_OPEN_COMMENTS);
            }
        }
        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        User user = userService.getById(userUid);
        // 判断字数是否超过限制
        if (commentVO.getContent().length() > SysConf.ONE_ZERO_TWO_FOUR) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.COMMENT_CAN_NOT_MORE_THAN_1024);
        }
        // 判断该用户是否被禁言
        if (user.getCommentStatus() == SysConf.ZERO) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.YOU_DONT_HAVE_PERMISSION_TO_SPEAK);
        }
        // 判断是否发送过多无意义评论
        String jsonResult = redisUtil.get(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid);
        if (!StringUtils.isEmpty(jsonResult)) {
            Integer count = Integer.valueOf(jsonResult);
            if (count >= Constants.NUM_FIVE) {
                return ResultUtil.result(SysConf.ERROR, MessageConf.PLEASE_TRY_AGAIN_IN_AN_HOUR);
            }
        }
        // 判断是否垃圾评论
        String content = commentVO.getContent();
        if (StringUtils.isCommentSpam(content)) {
            if (StringUtils.isEmpty(jsonResult)) {
                Integer count = 0;
                redisUtil.setEx(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid, count.toString(), 1, TimeUnit.HOURS);
            } else {
                redisUtil.incrBy(RedisConf.USER_PUBLISH_SPAM_COMMENT_COUNT + BaseSysConf.REDIS_SEGMENTATION + userUid, 1);
            }
            return ResultUtil.result(SysConf.ERROR, MessageConf.COMMENT_IS_SPAM);
        }
        // 判断被评论的用户，是否开启了评论邮件提醒
        if (StringUtils.isNotEmpty(commentVO.getToUserUid())) {
            User toUser = userService.getById(commentVO.getToUserUid());
            if (toUser.getStartEmailNotification() == SysConf.ONE) {
                Comment toComment = commentService.getById(commentVO.getToUid());
                if (toComment != null && StringUtils.isNotEmpty(toComment.getContent())) {
                    Map<String, String> map = new HashMap<>();
                    map.put(SysConf.EMAIL, toUser.getEmail());
                    map.put(SysConf.TEXT, commentVO.getContent());
                    map.put(SysConf.TO_TEXT, toComment.getContent());
                    map.put(SysConf.NICKNAME, user.getNickName());
                    map.put(SysConf.TO_NICKNAME, toUser.getNickName());
                    map.put(SysConf.USER_UID, toUser.getUid());
                    // 获取评论跳转的链接
                    String commentSource = toComment.getSource();
                    String url = new String();
                    switch (commentSource) {
                        case "ABOUT": {
                            url = dataWebsiteUrl + "about";
                        }
                        break;
                        case "BLOG_INFO": {
                            url = dataWebsiteUrl + "info?blogUid=" + toComment.getBlogUid();
                        }
                        break;
                        case "MESSAGE_BOARD": {
                            url = dataWebsiteUrl + "messageBoard";
                        }
                        break;
                        default: {
                            log.error("跳转到其它链接");
                        }
                    }
                    map.put(SysConf.URL, url);
                    // 发送评论邮件
                    rabbitMqUtil.sendCommentEmail(map);
                }
            }
        }

        Comment comment = new Comment();
        comment.setSource(commentVO.getSource());
        comment.setBlogUid(commentVO.getBlogUid());
        // 将Markdown转换成html
        String blogContent = FileUtils.markdownToHtml(commentVO.getContent());
        comment.setContent(blogContent);
        comment.setToUserUid(commentVO.getToUserUid());

        // 当该评论不是一级评论时，需要设置一级评论UID字段
        if (StringUtils.isNotEmpty(commentVO.getToUid())) {
            Comment toComment = commentService.getById(commentVO.getToUid());
            // 表示 toComment是非一级评论
            if (toComment != null && StringUtils.isNotEmpty(toComment.getFirstCommentUid())) {
                comment.setFirstCommentUid(toComment.getFirstCommentUid());
            } else {
                // 表示父评论是一级评论，直接获取UID
                comment.setFirstCommentUid(toComment.getUid());
            }
        }
//        else {
//            // 当该评论是一级评论的时候，说明是对 博客详情、留言板、关于我
//            // 判断是否开启邮件通知
//            SystemConfig systemConfig = systemConfigService.getConfig();
//            if (systemConfig != null && EOpenStatus.OPEN.equals(systemConfig.getStartEmailNotification())) {
//                if (StringUtils.isNotEmpty(systemConfig.getEmail())) {
//                    log.info("发送评论邮件通知");
//                    String sourceName = ECommentSource.valueOf(commentVO.getSource()).getName();
//                    String linkText = "<a href=\" " + getUrlByCommentSource(commentVO) + "\">" + sourceName + "</a>\n";
//                    String commentContent = linkText + "收到新的评论: " + commentVO.getContent();
//                    rabbitMqUtil.sendSimpleEmail(systemConfig.getEmail(), commentContent);
//                } else {
//                    log.error("网站没有配置通知接收的邮箱地址！");
//                }
//            }
//        }
        comment.setUserUid(commentVO.getUserUid());
        comment.setToUid(commentVO.getToUid());
        comment.setStatus(EStatus.ENABLE);
        comment.insert();
        //获取图片
        if (StringUtils.isNotEmpty(user.getAvatar())) {
            String pictureList = this.pictureFeignClient.getPicture(user.getAvatar(), SysConf.FILE_SEGMENTATION);
            if (webUtil.getPicture(pictureList).size() > 0) {
                user.setPhotoUrl(webUtil.getPicture(pictureList).get(0));
            }
        }
        comment.setUser(user);

        // 如果是回复某人的评论，那么需要向该用户Redis收件箱中中写入一条记录
        if (StringUtils.isNotEmpty(comment.getToUserUid())) {
            String redisKey = RedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + comment.getToUserUid();
            String count = redisUtil.get(redisKey);
            if (StringUtils.isNotEmpty(count)) {
                redisUtil.incrBy(redisKey, Constants.NUM_ONE);
            } else {
                redisUtil.setEx(redisKey, Constants.STR_ONE, 7, TimeUnit.DAYS);
            }
        }
        return ResultUtil.result(SysConf.SUCCESS, comment);
    }


    /**
     * 通过评论类型跳转到对应的页面
     *
     * @param commentVO
     * @return
     */
    private String getUrlByCommentSource(CommentVO commentVO) {
        String linkUrl = new String();
        String commentSource = commentVO.getSource();
        switch (commentSource) {
            case "ABOUT": {
                linkUrl = dataWebsiteUrl + "about";
            }
            break;
            case "BLOG_INFO": {
                linkUrl = dataWebsiteUrl + "info?blogUid=" + commentVO.getBlogUid();
            }
            break;
            case "MESSAGE_BOARD": {
                linkUrl = dataWebsiteUrl + "messageBoard";
            }
            break;
            default: {
                linkUrl = dataWebsiteUrl;
                log.error("跳转到其它链接");
            }
        }
        return linkUrl;
    }
    /**
     * 获取评论所有回复
     *
     * @param list
     * @param toCommentListMap
     * @return
     */
    private List<Comment> getCommentReplys(List<Comment> list, Map<String, List<Comment>> toCommentListMap) {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        } else {
            list.forEach(item -> {
                String commentUid = item.getUid();
                List<Comment> replyCommentList = toCommentListMap.get(commentUid);
                List<Comment> replyComments = getCommentReplys(replyCommentList, toCommentListMap);
                item.setReplyList(replyComments);
            });
            return list;
        }
    }

    @PostMapping("/readUserReceiveCommentCount")
    public String readUserReceiveCommentCount(HttpServletRequest request) {
        log.info("阅读用户接收的评论数");
        // 判断用户是否登录
        if (request.getAttribute(SysConf.USER_UID) != null) {
            String userUid = request.getAttribute(SysConf.USER_UID).toString();
            String redisKey = RedisConf.USER_RECEIVE_COMMENT_COUNT + Constants.SYMBOL_COLON + userUid;
            redisUtil.delete(redisKey);
        }
        return ResultUtil.successWithMessage("阅读成功");
    }

}

