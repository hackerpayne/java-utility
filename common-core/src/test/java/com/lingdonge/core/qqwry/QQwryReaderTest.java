package com.lingdonge.core.qqwry;

import com.lingdonge.core.thirdparty.qqwry.Location;
import com.lingdonge.core.thirdparty.qqwry.QQwryReader;
import com.lingdonge.core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by kyle on 17/3/1.
 */
@Slf4j
public class QQwryReaderTest {

    private static String dataFile = new File(Utils.CurrentDir, "QQWry.DAT").getPath();

    /**
     * @throws Exception
     */
    public static void getAll() throws Exception {
        final QQwryReader gen = new QQwryReader(dataFile);
        List<Location> listData = gen.readAll();

        log.info("共查询到数据共计：" + listData.size());

        for (int i = 0; i < 100; i++) {
            log.info("第" + i + "条数据为：" + listData.get(i).toString());
        }
    }

    @Test
    public static void getByCity() throws Exception {
        final QQwryReader gen = new QQwryReader(dataFile);

        List<Location> listData = gen.readByCity("眉山");

        log.info("共查询到数据共计：" + listData.size());

        for (int i = 0; i < 100; i++) {
            log.info("第" + i + "条数据为：" + listData.get(i).toString());
        }
    }
}