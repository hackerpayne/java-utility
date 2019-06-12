package com.lingdonge.core.file.yaml;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class YamlUtilTest {

    @Test
    public void test() throws IOException {

        Map map = YamlUtil.loadYamlAsMap("application-local.yml");
        System.out.println(map);

        Properties properties = YamlUtil.loadYamlAsProperties("hbase.yml");
        System.out.println("=====properties=====");
        System.out.println(properties.getProperty("hbase.client.scanner.timeout.period"));

        Object obj = YamlUtil.loadYamlAsObj("application-local.yml");
        System.out.println(obj);

//        ModelProfile profile = loadYamlAs("application-local.yml", ModelProfile.class);
//        System.out.println(profile.toString());

    }

}