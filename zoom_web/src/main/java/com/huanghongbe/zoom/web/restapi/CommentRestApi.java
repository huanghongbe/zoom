package com.huanghongbe.zoom.web.restapi;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * 评论RestApi
 *
 */
@RestController
@RefreshScope
@RequestMapping("/web/comment")
@Api(value = "评论相关接口", tags = {"评论相关接口"})
@Slf4j
public class CommentRestApi {

    @Autowired
    private RedisUtil redisUtil;

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

}

