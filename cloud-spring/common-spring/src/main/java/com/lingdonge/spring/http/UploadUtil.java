package com.lingdonge.spring.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传服务
 */
@Slf4j
public class UploadUtil {
    /**
     * 功能：上传图片
     * 例：
     * String uploadPath = "static/images/";  // 服务器上上传文件的相对路径
     * String physicalUploadPath = getClass().getClassLoader().getResource(uploadPath).getPath();  // 服务器上上传文件的物理路径
     * String imageURL = imageUploadService.uploadImage( image, uploadPath, physicalUploadPath );
     * File imageFile = new File(physicalUploadPath + image.getOriginalFilename() );
     *
     * @param file               文件
     * @param uploadPath         服务器上上传文件的路径
     * @param physicalUploadPath 服务器上上传文件的物理路径
     * @return 上传文件的 URL相对地址
     */
    public String uploadImage(MultipartFile file, String uploadPath, String physicalUploadPath) {

        String filePath = physicalUploadPath + file.getOriginalFilename();

        try {
            File targetFile = new File(filePath);
            FileUtils.writeByteArrayToFile(targetFile, file.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return uploadPath + "/" + file.getOriginalFilename();
    }

}
