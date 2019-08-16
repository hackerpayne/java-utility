package com.lingdonge.spring.restful;

import com.lingdonge.spring.bean.response.RespPage;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.List;

public class RespPageTest {

    @Test
    public void testFailResult() throws Exception {
        System.out.println("失败结果");
//        System.out.println(RespPage.fail(ResultCode.SuccessCode, "TestResutl"));
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
//        System.out.println(RespPage.fail(ResultCode.SuccessCode, "TestResutl", listData));
    }

    @Test
    public void testSuccessResult() throws Exception {
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

        System.out.println("成功结果");
        System.out.println(RespPage.success(100, 10, 1, 102, listData));
    }

    @Test
    public void testSuccessResultPage() throws Exception {
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

//        Page page = new Page();
//        page.setPageNumber(1);
//        page.setPageSize(100);
//        page.setTotalPage(1000);
//        page.setTotalRow(1000000);
//
//        page.setResult(listData);
//
//        System.out.println("Page成功结果");
//        System.out.println(RespPage.fail(page));
    }

}