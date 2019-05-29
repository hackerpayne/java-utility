package com.lindonge.core.thirdparty.qqwry;

import com.lindonge.core.model.ModelIPLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kyle on 2017/6/16.
 */
public class QQWryTest {

    private static final Logger logger = LoggerFactory.getLogger(QQWryTest.class);

    public static void main(String[] args) {

        ModelIPLocation location=QQwryUtils.getIPLocation("210.12.126.10");

        System.out.println(location.toString());

    }

}
