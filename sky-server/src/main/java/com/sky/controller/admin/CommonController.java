package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 是一个通用的接口，上传文件(图片)-都可以用这个接口
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {


    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传功能
     * @param file
     * @return
     */
    //因为要返回文件路径-根据开发接口文档-需要返回URL路径-然后前端通过这个路径去查找文件？
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> uploadFill (MultipartFile file) throws IOException {
        log.info("文件上传：{}",file);
        String filePath = aliOssUtil.upload(file.getBytes(), file.getOriginalFilename());
        if (filePath == null) {
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
        return Result.success(filePath);
    }

}
