package com.lingdonge.spider.webmagic.pipeline;

import com.lingdonge.spider.webmagic.Task;
import com.lingdonge.spider.webmagic.model.HasKey;
import com.lingdonge.spider.webmagic.utils.FilePersistentBase;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Store results objects (page models) to files in plain format.<br>
 * Use bean.getKey() as file name if the bean implements HasKey.<br>
 * Otherwise use SHA1 as file name.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.3.0
 */
public class FilePageModelPipeline extends FilePersistentBase implements PageModelPipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * new JsonFilePageModelPipeline with default path "/data/spider/"
     */
    public FilePageModelPipeline() {
        setPath("/data/spider/");
    }

    public FilePageModelPipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(Object o, Task task) {
        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        try {
            String filename;
            if (o instanceof HasKey) {
                filename = path + ((HasKey) o).key() + ".html";
            } else {
                filename = path + DigestUtils.md5Hex(ToStringBuilder.reflectionToString(o)) + ".html";
            }
            PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(filename)));
            printWriter.write(ToStringBuilder.reflectionToString(o));
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
