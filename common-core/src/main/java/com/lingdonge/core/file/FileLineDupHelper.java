package com.lingdonge.core.file;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;

/**
 * 文件内容、关键词、大内容等 去重过滤处理方案
 * Created by kyle on 17/4/24.
 */
@Slf4j
public class FileLineDupHelper {


    /**
     * 使用Bloom Filter判断是否重复，把不重复的内容输入到新的文件里面
     *
     * @param sourceFile 要处理的源文件，一行一条
     * @param saveFile   要保存结果的文件，一行一条
     * @throws IOException
     */
    public static void FileDupdateTo(String sourceFile, String saveFile) throws IOException {

        final BloomFilter<String> dealIdBloomFilter = BloomFilter.create(new Funnel<String>() {
            @Override
            public void funnel(String from, PrimitiveSink into) {
                into.putString(from, Charsets.UTF_8);
            }
            //0.0000001d为错误率， 9000000 为预估元素的个数， 我第一次测试用了大概9000000行字符串的文本
        }, 9000000, 0.0000001d);


        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(sourceFile)), "utf-8"));
        String line;
        int i = 0;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            final boolean put = dealIdBloomFilter.put(line);
            if (put) {
                sb.append(line).append("\n");
                i++;
            }
            if (i % 1000 == 0) {
                //保存虑重后的文本。
                FileUtils.write(new File(saveFile), sb.toString(), Charsets.UTF_8, true);
                sb = new StringBuilder();
            }
        }

        // 如果最后里面还有内容，就继续保存
        if (StringUtils.isNotEmpty(sb.toString())) {
            //保存虑重后的文本。
            FileUtils.write(new File(saveFile), sb.toString(), Charsets.UTF_8, true);
        }

    }


    /**
     * 使用Spark对本地内容进行过滤重复处理，并保存到新的结果内
     * 需要HadoopCommon支持
     *
     * @param sourceFile 需要处理的文件
     * @param saveFile   结果保存文件
     */
//    public static void DupFileBySpark(String sourceFile, String saveFile) {
//        System.setProperty("hadoop.home.dir", "/usr/local/hadoop/hadoop-web-2.2.0/");
//
//        SparkConf conf = new SparkConf().setAppName("Text String Distinct").setMaster("local").set("spark.executor.memory", "1g");
//        JavaSparkContext sc = new JavaSparkContext(conf);
//
//        //读取需要虑重的文本文件
//        JavaRDD<String> textFile = sc.textFile(sourceFile);
//        final JavaRDD<String> distinct = textFile.distinct();
//
//        final long count = distinct.count();//去除重复后的结果数量
//
//        logger.info("After DupFileBySpark File has a line of :" + count);
//
//        //保存率重后的文本文件
//        distinct.coalesce(1).saveAsTextFile(saveFile);
//
//    }
}
