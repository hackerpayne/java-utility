package com.lingdonge.alibaba.oss.service;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.UUID;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.lingdonge.alibaba.oss.configuration.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * OSS连接处理服务
 */
@Slf4j
public class OssClientService {

    private OssProperties ossProperties;

    /**
     * 构造配置文件
     *
     * @param ossProperties
     */
    public OssClientService(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    /**
     * 删除文件
     *
     * @param key
     */
    public void deleteFile(String key) {
        log.info("oss deleting file,key = {}", key);
        OSSClient client = new OSSClient(ossProperties.getUploadEnpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
        client.deleteObject(ossProperties.getBucketName(), key);
    }

    /**
     * 上传结果返回最后存储的path
     *
     * @param inputStream
     * @param size
     * @param storePath
     * @return
     * @throws FileNotFoundException
     */
    public String upload(InputStream inputStream, long size, String storePath) throws FileNotFoundException {
        long beginTime = System.currentTimeMillis();
        OSSClient client = new OSSClient(ossProperties.getUploadEnpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(size);
        if (StringUtils.startsWith(storePath, "/")) {
            storePath = StringUtils.substringAfter(storePath, "/");
        }

        storePath = ossProperties.getBaseDir() + "/" + storePath;
        PutObjectResult result = client.putObject(ossProperties.getBucketName(), storePath, inputStream, meta);
        long endTime = System.currentTimeMillis();
        log.info("上传{}耗时：{}ms", storePath, endTime - beginTime);
        IoUtil.close(inputStream);
        return storePath;
    }

    /**
     * 获取生成的文件名
     *
     * @param storeFile
     * @return
     */
    public String getDownUrl(String storeFile) {
        return "https://" + ossProperties.getBucketName() + "." + ossProperties.getDownloadEnpoint() + "/" + storeFile;
    }

    /**
     * 获取生成的OSS根路径
     *
     * @return
     */
    public String getOssBase() {
        return "https://" + ossProperties.getBucketName() + "." + ossProperties.getDownloadEnpoint();
    }

    /**
     * @param storeFile
     * @return
     * @throws FileNotFoundException //获取Object的输入流 InputStream objectContent =
     *                               object.getObjectContent(); //处理Object ... // 关闭流
     *                               objectContent.close();
     */
    public OSSObject getInputStream(String storeFile) {
        OSSClient client = new OSSClient(ossProperties.getUploadEnpoint(), ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret());
        return client.getObject(ossProperties.getBucketName(), storeFile);
    }

    /**
     * 下载文件
     *
     * @param storeFile
     * @param savePath
     * @param fileName
     * @throws IOException
     */
    public String download(String storeFile, String savePath, String fileName) throws IOException {
        long beginTime = System.currentTimeMillis();// 开始时间
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        OutputStream outstream = null;
        String outputFile = "";

        if (StringUtils.startsWith(storeFile, "/")) {
            storeFile = StringUtils.substringAfter(storeFile, "/");
        }

        OSSObject object = getInputStream(storeFile);
        InputStream objectInputStream = object.getObjectContent();
        try {
            String ext = FilenameUtils.getExtension(storeFile);
            File file = new File(savePath + File.separator + fileName + "." + ext);
            if (!file.getParentFile().exists()) {
                file.mkdirs();
            }
            outputFile = file.getAbsolutePath();
            outstream = new FileOutputStream(file);
            bis = new BufferedInputStream(objectInputStream);
            bos = new BufferedOutputStream(outstream);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
            bos.flush();
        } catch (Exception e) {
            log.error("文件没有找到：{}", e);
        } finally {
            IoUtil.close(bis);
            IoUtil.close(bos);
            IoUtil.close(objectInputStream);
        }
        long endTime = System.currentTimeMillis();
        log.info("下载{}耗时：{}ms", savePath + fileName, endTime - beginTime);
        return outputFile;
    }

    /**
     * 下载文件作为临时文件存储在硬盘里，使用后请手动删除临时文件
     *
     * @param storePath
     * @return
     * @throws IOException
     */
    public String download(String storePath) throws IOException {
        String logoSavePath = ossProperties.getTmpDir();
        String logoFileName = UUID.randomUUID().toString();
        String logoFileAbsPath = download(storePath, logoSavePath, logoFileName);
        return logoFileAbsPath;
    }

}
