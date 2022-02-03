package com.huanghongbe.zoom.picture.restapi;

import com.huanghongbe.zoom.picture.service.FileService;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private FileService fileService;
    @GetMapping("/getPicture")
    public String getPicture(
            @ApiParam(name = "fileIds", value = "文件ids", required = false) @RequestParam(name = "fileIds", required = false) String fileIds,
            @ApiParam(name = "code", value = "切割符", required = false) @RequestParam(name = "code", required = false) String code) {
        log.info("获取图片信息: {}", fileIds);
        return fileService.getPicture(fileIds, code);
    }

}
