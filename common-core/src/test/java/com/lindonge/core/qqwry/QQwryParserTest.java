package com.lindonge.core.qqwry;

import com.lindonge.core.util.Utils;
import com.lindonge.core.thirdparty.qqwry.QQwryUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Created by kyle on 17/3/1.
 */
public class QQwryParserTest {

    private static final Logger logger = LoggerFactory.getLogger(QQwryParserTest.class);

    public static String randomIp() {
        return String.format("%d.%d.%d.%d", RandomUtils.nextInt(1, 230), RandomUtils.nextInt(1, 255), RandomUtils.nextInt(1, 255), RandomUtils.nextInt(1, 255));
    }


    @Test
    public void Test() {

        logger.info("路径为："+ Utils.CurrentDir+"/QQWry.DAT");

        System.out.println(QQwryUtils.getAddrInfo("104.41.42.230"));
        System.out.println(QQwryUtils.getAddrInfo(randomIp()));
        System.out.println(QQwryUtils.getAddrInfo("121.46.120.85"));
        logger.info(QQwryUtils.getAddrInfo("103.3.120.2").toString());
    }


}