package com.huanghongbe.zoom.picture.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.base.vo.FileVO;
import com.huanghongbe.zoom.commons.entity.File;
import com.huanghongbe.zoom.commons.entity.SystemConfig;
import com.huanghongbe.zoom.picture.enums.MessageConf;
import com.huanghongbe.zoom.picture.enums.SQLConf;
import com.huanghongbe.zoom.picture.enums.SysConf;
import com.huanghongbe.zoom.picture.mapper.FileMapper;
import com.huanghongbe.zoom.picture.service.FileService;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-04 4:59
 */
@Service
//@DubboService
public class FileServiceImpl extends SuperServiceImpl<FileMapper, File> implements FileService {
    @Autowired
    private FileService fileService;
    @Override
    public String cropperPicture(List<MultipartFile> multipartFileList) {
        return null;
    }

    @Override
    public String getPicture(String fileIds, String code) {
        if (StringUtils.isEmpty(code)) {
            code = Constants.SYMBOL_COMMA;
        }
        if (StringUtils.isEmpty(fileIds)) {
            log.error(MessageConf.PICTURE_UID_IS_NULL);
            return ResultUtil.result(SysConf.ERROR, MessageConf.PICTURE_UID_IS_NULL);
        } else {
            List<Map<String, Object>> list = new ArrayList<>();
            List<String> changeStringToString = StringUtils.changeStringToString(fileIds, code);
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(SQLConf.UID, changeStringToString);
            List<File> fileList = fileService.list(queryWrapper);
            if (fileList.size() > 0) {
                fileList.stream().filter(Objects::nonNull).forEach(file -> {
                    Map<String, Object> remap = new HashMap<>();
                    // 获取七牛云地址
                    remap.put(SysConf.QI_NIU_URL, file.getQiNiuUrl());
                    // 获取Minio对象存储地址
                    remap.put(SysConf.MINIO_URL, file.getMinioUrl());
                    // 获取本地地址
                    remap.put(SysConf.URL, file.getPicUrl());
                    // 后缀名，也就是类型
                    remap.put(SysConf.EXPANDED_NAME, file.getPicExpandedName());
                    remap.put(SysConf.FILE_OLD_NAME, file.getFileOldName());
                    //名称
                    remap.put(SysConf.NAME, file.getPicName());
                    remap.put(SysConf.UID, file.getUid());
                    remap.put(SQLConf.FILE_OLD_NAME, file.getFileOldName());
                    list.add(remap);
                });
            }
            return ResultUtil.result(SysConf.SUCCESS, list);
        }
    }

    @Override
    public String batchUploadFile(HttpServletRequest request, List<MultipartFile> multipartFileList, SystemConfig systemConfig) {
        return null;
    }

    @Override
    public String uploadPictureByUrl(FileVO fileVO) {
        return null;
    }

    @Override
    public Object ckeditorUploadFile(HttpServletRequest request) {
        return null;
    }

    @Override
    public Object ckeditorUploadCopyFile() {
        return null;
    }

    @Override
    public Object ckeditorUploadToolFile(HttpServletRequest request) {
        return null;
    }
}
