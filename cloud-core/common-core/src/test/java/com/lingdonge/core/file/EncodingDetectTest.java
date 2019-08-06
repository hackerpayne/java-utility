package com.lingdonge.core.file;

import org.junit.Test;

public class EncodingDetectTest {

    @Test
    public void test() {
        String file = "/Users/kyle/Downloads/958896357874073600.txt";
        String encode = EncodingDetect.getJavaEncode(file);
        System.out.println(encode);

        file = "/Users/kyle/Downloads/orm_init_table.sql";
        encode = EncodingDetect.getJavaEncode(file);
        System.out.println(encode);
        EncodingDetect.readFile(file, encode);
    }
}