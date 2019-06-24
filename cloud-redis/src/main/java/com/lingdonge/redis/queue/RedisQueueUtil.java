package com.lingdonge.redis.queue;

import com.lingdonge.redis.util.RedisConnUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class RedisQueueUtil {

    @Resource
    private StringRedisTemplate redisTemplate;

    /**
     * @param redisProperties
     */
    public RedisQueueUtil(RedisProperties redisProperties) {
        this.redisTemplate = (StringRedisTemplate) RedisConnUtil.getRedisTemplate(redisProperties);
    }

    public void pop(String redisKey) {
        this.redisTemplate.opsForSet().pop(redisKey);
    }

    public Long add(String redisKey, String... item) {
        return this.redisTemplate.opsForSet().add(redisKey, item);
    }

    /**
     * 把文件每一行存入Redis的set里面，指定Key
     *
     * @param file
     * @param redisKey
     */
    public void addFile(File file, String redisKey) {

        try {
            log.info("读取数据，来自文件：" + file.getAbsolutePath());
            List<String> fileLines = FileUtils.readLines(file, "utf-8");

            for (String line : fileLines) {
                if (StringUtils.isNotEmpty(line)) {
                    redisTemplate.opsForSet().add(redisKey, line.trim());//存到URL列表里面
                }
            }
            log.info("文件行数：" + fileLines.size());
        } catch (IOException e) {
            log.error("文件读取出错", e);
        }
    }

}
