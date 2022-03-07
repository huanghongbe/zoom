package com.huanghongbe.zoom.picture.restapi;

import com.huanghongbe.zoom.base.vo.FileVO;
import com.huanghongbe.zoom.commons.entity.SystemConfig;
import com.huanghongbe.zoom.picture.service.FileService;
import com.huanghongbe.zoom.picture.utils.FeignUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-04 4:55
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileRestApi {

    @Autowired
    private FeignUtil feignUtil;

    @RequestMapping(value = "/cropperPicture", method = RequestMethod.POST)
    public String cropperPicture(@RequestParam("file") MultipartFile file) {
        List<MultipartFile> multipartFileList = new ArrayList<>();
        multipartFileList.add(file);
        return fileService.cropperPicture(multipartFileList);
    }
    @Autowired
    private FileService fileService;
    @GetMapping("/getPicture")
    public String getPicture(
            @RequestParam(name = "fileIds", required = false) String fileIds,
            @RequestParam(name = "code", required = false) String code) {
        log.info("获取图片信息: {}", fileIds);
        return fileService.getPicture(fileIds, code);
    }

    @PostMapping("/pictures")
    public synchronized Object uploadPics(HttpServletRequest request, List<MultipartFile> filedatas) {
        // 获取系统配置文件
        SystemConfig systemConfig = feignUtil.getSystemConfig();
        return fileService.batchUploadFile(request, filedatas, systemConfig);
    }

    @PostMapping("/uploadPicsByUrl")
    public Object uploadPicsByUrl(@RequestBody FileVO fileVO, BindingResult result) {
        return fileService.uploadPictureByUrl(fileVO);
    }

    @RequestMapping(value = "/ckeditorUploadFile", method = RequestMethod.POST)
    public Object ckeditorUploadFile(HttpServletRequest request) {
        return fileService.ckeditorUploadFile(request);
    }

    @RequestMapping(value = "/ckeditorUploadCopyFile", method = RequestMethod.POST)
    public synchronized Object ckeditorUploadCopyFile() {
        return fileService.ckeditorUploadCopyFile();
    }

    @RequestMapping(value = "/ckeditorUploadToolFile", method = RequestMethod.POST)
    public Object ckeditorUploadToolFile(HttpServletRequest request) {
        return fileService.ckeditorUploadToolFile(request);
    }

}
