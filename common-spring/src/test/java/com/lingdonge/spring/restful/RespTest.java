package com.lingdonge.spring.restful;

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

public class RespTest {

    @Test
    public void testFailResult() throws Exception {
        System.out.println("失败结果");
        System.out.println(Resp.fail(ResultCode.SuccessCode, "TestResutl"));
    }

    @Test
    public void testFailResult1() throws Exception {
        List<ModelTest> listData = Lists.newArrayList();
        ModelTest modelTest = new ModelTest();
        modelTest.setTitle("title");
        modelTest.setValue("hahah");
        modelTest.setContent("content");
        listData.add(modelTest);

        modelTest = new ModelTest();
        modelTest.setTitle("title2");
        modelTest.setValue("hahah2");
        modelTest.setContent("content2");
        listData.add(modelTest);

        System.out.println("失败结果2");
        System.out.println(Resp.fail(ResultCode.SuccessCode, "TestResutl", listData));
    }

    @Test
    public void testSuccessResult() throws Exception {
        ModelTest modelTest = new ModelTest();
        modelTest.setTitle("title");
        modelTest.setValue("hahah");
        modelTest.setContent("content");

        System.out.println("成功结果");
        System.out.println(Resp.success(modelTest));
    }

    @Test
    public void testFailResult2() throws Exception {

        System.out.println("失败结果3");
        System.out.println(Resp.fail("只有失败信息"));
    }

    @Test
    public void testSuccessResult1() throws Exception {
        System.out.println("成功结果3");
        System.out.println(Resp.success("只有成功信息"));
    }

    @Test
    public void testJsonSuccessResult1() throws Exception {
        System.out.println("成功结果3");
        System.out.println(Resp.success("只有成功信息").jsonString());
    }

}