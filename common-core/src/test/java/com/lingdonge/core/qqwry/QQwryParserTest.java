package com.lingdonge.core.qqwry;

import com.lingdonge.core.thirdparty.qqwry.QQwryUtils;
import com.lingdonge.core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

/**
 * Created by kyle on 17/3/1.
 */
@Slf4j
public class QQwryParserTest {

    public static String randomIp() {
        return String.format("%d.%d.%d.%d", RandomUtils.nextInt(1, 230), RandomUtils.nextInt(1, 255), RandomUtils.nextInt(1, 255), RandomUtils.nextInt(1, 255));
    }


    @Test
    public void Test() {

        log.info("路径为：" + Utils.CurrentDir + "/QQWry.DAT");

        System.out.println(QQwryUtils.getAddrInfo("104.41.42.230"));
        System.out.println(QQwryUtils.getAddrInfo(randomIp()));
        System.out.println(QQwryUtils.getAddrInfo("121.46.120.85"));
        log.info(QQwryUtils.getAddrInfo("103.3.120.2").toString());
    }


}