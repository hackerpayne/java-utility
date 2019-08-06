package com.lingdonge.http.webmagic.pipeline;


import com.lingdonge.http.webmagic.ResultItems;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.utils.FilePersistentBase;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by pantheon on 2017/3/13.
 */
public class FileDownloadPipeline extends FilePersistentBase implements Pipeline {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public FileDownloadPipeline(String path) {
        this.setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        //确保文件夹存在
        String path = this.path + PATH_SEPERATOR;
        this.checkAndMakeParentDirecotry(path + PATH_SEPERATOR);

        logger.info("url:\t" + resultItems.getRequest().getUrl());

        List<String> imageTitles = new ArrayList<String>();
        List<String> imageUrls = new ArrayList<String>();

        //找到匹配信息
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            String key = entry.getKey();

            if ("imageTitles".equals(key)) {
                imageTitles = (ArrayList<String>) entry.getValue();
            } else if ("imageUrls".equals(key)) {
                imageUrls = (ArrayList<String>) entry.getValue();
            }

        }

        StopWatch stopWatch = new StopWatch("文件下载");
        stopWatch.start("微信图片下载");

        if (imageTitles.size() == imageUrls.size()) {
            logger.info("imageTitles.size() == imageUrls.size(),信息匹配成功!");
            for (int i = 0; i < imageUrls.size(); i++) {
                //开始下载
                this.downloadFile(imageUrls.get(i));
            }
        } else {
            logger.error("imageTitles.size() != imageUrls.size(),信息匹配失败!");
        }

        stopWatch.stop();
        logger.info(stopWatch.prettyPrint());

    }

    /**
     * 根据URL下载文件
     *
     * @param urlString
     */
    private void downloadFile(String urlString) {
        this.downloadFile(urlString, System.currentTimeMillis() + "");
    }

    /**
     * 根据URL下载文件
     *
     * @param urlString
     */
    private void downloadFile(String urlString, String fileName) {
        int index = urlString.lastIndexOf(".");
        String filePath = urlString.substring(index, urlString.length());

        //获取文件名称,格式： fileName.jpg
        filePath = this.getPath() + fileName + filePath;

        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(filePath);
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            IOUtils.copy(conn.getInputStream(), outputStream);

        } catch (MalformedURLException e) {
            logger.error("FileDownloadPipeline-downloadFile-MalformedURLException-error:", e);
        } catch (FileNotFoundException e) {
            logger.error("FileDownloadPipeline-downloadFile-FileNotFoundException-error:", e);
        } catch (IOException e) {
            logger.error("FileDownloadPipeline-downloadFile-IOException-error:", e);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.error("FileDownloadPipeline-downloadFile-IOException-error:", e);
            }
        }
    }
}