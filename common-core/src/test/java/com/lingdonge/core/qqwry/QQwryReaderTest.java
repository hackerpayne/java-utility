package com.lingdonge.core.qqwry;
import com.lingdonge.core.util.Utils;
import com.lingdonge.core.thirdparty.qqwry.Location;
import com.lingdonge.core.thirdparty.qqwry.QQwryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

/**
 * Created by kyle on 17/3/1.
 */
public class QQwryReaderTest {
    private static final Logger logger = LoggerFactory.getLogger(QQwryParserTest.class);

    private static String dataFile = new File(Utils.CurrentDir, "QQWry.DAT").getPath();

    /**
     * @throws Exception
     */
    public static void getAll() throws Exception {
        final QQwryReader gen = new QQwryReader(dataFile);
        List<Location> listData = gen.readAll();

        logger.info("共查询到数据共计：" + listData.size());

        for (int i = 0; i < 100; i++) {
            logger.info("第" + i + "条数据为：" + listData.get(i).toString());
        }
    }

    @Test
    public static void getByCity() throws Exception {
        final QQwryReader gen = new QQwryReader(dataFile);

        List<Location> listData = gen.readByCity("眉山");

        logger.info("共查询到数据共计：" + listData.size());

        for (int i = 0; i < 100; i++) {
            logger.info("第" + i + "条数据为：" + listData.get(i).toString());
        }
    }
}