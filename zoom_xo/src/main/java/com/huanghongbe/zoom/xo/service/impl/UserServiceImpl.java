package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.global.BaseSQLConf;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.User;
import com.huanghongbe.zoom.commons.feign.PictureFeignClient;
import com.huanghongbe.zoom.utils.MD5Utils;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.MessageConf;
import com.huanghongbe.zoom.xo.enums.SQLConf;
import com.huanghongbe.zoom.xo.enums.SysConf;
import com.huanghongbe.zoom.xo.mapper.UserMapper;
import com.huanghongbe.zoom.xo.service.SysParamsService;
import com.huanghongbe.zoom.xo.service.UserService;
import com.huanghongbe.zoom.xo.utils.WebUtil;
import com.huanghongbe.zoom.xo.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-07 23:22
 */
@Service
public class UserServiceImpl extends SuperServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserService userService;
    @Autowired
    private WebUtil webUtil;
    @Resource
    private PictureFeignClient pictureFeignClient;
    @Autowired
    private SysParamsService sysParamsService;
    @Override
    public User insertUserInfo(HttpServletRequest request, String response) {
        return null;
    }

    @Override
    public User getUserBySourceAnduuid(String source, String uuid) {
        return null;
    }

    @Override
    public Integer getUserCount(int status) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.STATUS, status);
        return userService.count(queryWrapper);
    }

    @Override
    public User serRequestInfo(User user) {
        return null;
    }

    @Override
    public List<User> getUserListByIds(List<String> ids) {
        return null;
    }

    @Override
    public IPage<User> getPageList(UserVO userVO) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 查询用户名
        if (StringUtils.isNotEmpty(userVO.getKeyword()) && !StringUtils.isEmpty(userVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.USER_NAME, userVO.getKeyword().trim()).or().like(SQLConf.NICK_NAME, userVO.getKeyword().trim());
        }
        if (StringUtils.isNotEmpty(userVO.getSource()) && !StringUtils.isEmpty(userVO.getSource().trim())) {
            queryWrapper.eq(SQLConf.SOURCE, userVO.getSource().trim());
        }
        if (userVO.getCommentStatus() != null) {
            queryWrapper.eq(SQLConf.COMMENT_STATUS, userVO.getCommentStatus());
        }

        if(StringUtils.isNotEmpty(userVO.getOrderByAscColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(userVO.getOrderByAscColumn())).toString();
            queryWrapper.orderByAsc(column);
        } else if(StringUtils.isNotEmpty(userVO.getOrderByDescColumn())) {
            // 将驼峰转换成下划线
            String column = StringUtils.underLine(new StringBuffer(userVO.getOrderByDescColumn())).toString();
            queryWrapper.orderByDesc(column);
        } else {
            queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        }

        queryWrapper.select(User.class, i -> !i.getProperty().equals(SQLConf.PASS_WORD));
//        Page<User> page = new Page<>();
//        page.setCurrent(userVO.getCurrentPage());
//        page.setSize(userVO.getPageSize());
        queryWrapper.ne(SQLConf.STATUS, EStatus.DISABLED);
        IPage<User> pageList = userService.page(
                new Page<>(userVO.getCurrentPage(),userVO.getPageSize()), queryWrapper);

        List<User> list = pageList.getRecords();

        final StringBuffer fileUids = new StringBuffer();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                fileUids.append(item.getAvatar() + SysConf.FILE_SEGMENTATION);
            }
        });

        Map<String, String> pictureMap = new HashMap<>();
        String pictureResult = null;

        if (fileUids != null) {
            pictureResult = this.pictureFeignClient.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureResult);

        picList.forEach(item -> {
            pictureMap.put(item.get(SQLConf.UID).toString(), item.get(SQLConf.URL).toString());
        });

        list.forEach(item->{
            //获取图片
            if (StringUtils.isNotEmpty(item.getAvatar())) {
                List<String> pictureUidsTemp = StringUtils.changeStringToString(item.getAvatar(), SysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(picture -> {
                    if (pictureMap.get(picture) != null && pictureMap.get(picture) != "") {
                        pictureListTemp.add(pictureMap.get(picture));
                    }
                });
                if (pictureListTemp.size() > 0) {
                    item.setPhotoUrl(pictureListTemp.get(0));
                }
            }
        });
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addUser(UserVO userVO) {
        User user = new User();
        // 字段拷贝【将userVO中的内容拷贝至user】
        BeanUtils.copyProperties(userVO, user, SysConf.STATUS);
        String defaultPassword = sysParamsService.getSysParamsValueByKey(SysConf.SYS_DEFAULT_PASSWORD);
        user.setPassWord(MD5Utils.string2MD5(defaultPassword));
        user.setSource("ZOOM");
        user.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editUser(UserVO userVO) {
        User user = userService.getById(userVO.getUid());
        user.setUserName(userVO.getUserName());
        user.setEmail(userVO.getEmail());
        user.setStartEmailNotification(userVO.getStartEmailNotification());
        user.setOccupation(userVO.getOccupation());
        user.setGender(userVO.getGender());
        user.setQqNumber(userVO.getQqNumber());
        user.setSummary(userVO.getSummary());
        user.setBirthday(userVO.getBirthday());
        user.setAvatar(userVO.getAvatar());
        user.setNickName(userVO.getNickName());
        user.setUserTag(userVO.getUserTag());
        user.setCommentStatus(userVO.getCommentStatus());
        user.setUpdateTime(new Date());
        user.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteUser(UserVO userVO) {
        User user = userService.getById(userVO.getUid());
        user.setStatus(EStatus.DISABLED);
        user.setUpdateTime(new Date());
        user.updateById();
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String resetUserPassword(UserVO userVO) {
        String defaultPassword = sysParamsService.getSysParamsValueByKey(SysConf.SYS_DEFAULT_PASSWORD);
        User user = userService.getById(userVO.getUid());
        user.setPassWord(MD5Utils.string2MD5(defaultPassword));
        user.setUpdateTime(new Date());
        user.updateById();
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }
}
