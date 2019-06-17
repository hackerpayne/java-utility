package com.lingdonge.core.http;


import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Kyle on 16/8/29.
 */
public class URLParserTest {

    private static URLParser parser;

    @BeforeClass
    public static void Init() {
        parser = new URLParser("https://www.yirendai.com/LandingPage/mhd/mhd2/?utm_source=bd-pc-ss&utm_medium=SEM_borrower&utm_campaign=%C6%B7%C5%C6%B4%CA&utm_content=%D2%CB%C8%CB%B4%FB-%BA%CB%D0%C4&utm_term=%D2%CB%C8%CB%B4%FB&utm_cparameters=baiduPC03#downloading", "gb2312");
    }

    @Test
    public void getCleanUrl() throws Exception {

        String clean = parser.getCleanUrl();
        System.out.println(clean);
    }

    @Test
    public void getQueryStr() throws Exception {

    }

    @Test
    public void getFragement() throws Exception {

    }

    @Test
    public void parseQuery() throws Exception {

    }

    @Test
    public void parseUrlModel() throws Exception {
        System.out.println("parseUrlModel");

        System.out.println(parser.parseUrlModel());
    }

}