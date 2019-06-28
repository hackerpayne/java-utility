package com.lingdonge.excel;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.junit.Test;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EasyExcelUtilTest {

    @Test
    public void readExcel() {
        String fileName = "/Users/kyle/Downloads/需监控的地址_0625.xlsx";
        InputStream inputStream = FileUtil.getInputStream(fileName);
        List<Object> data = EasyExcelFactory.read(inputStream, new Sheet(1, 1));
        IoUtil.close(inputStream);

        data.forEach(item -> {
            List line = (List) item;
            String sql = StrUtil.format("INSERT INTO `alert_rule`( `alert_account_id`, `alert_type`, `name`, `url`, `alert_interval`, `user_id`, `keyword`, `status`, `create_time`, `update_time`) VALUES ( 3, 1, '{}', '{}', 5, 1, NULL, 1, '2019-06-25 14:09:23', NULL);", line.get(2), line.get(1));
            System.out.println(sql);
        });
        System.out.println(data);
    }

    @Test
    public void testExcel2003NoModel() {
        InputStream inputStream = FileUtil.getInputStream("loan1.xls");
        try {
            // 解析每行结果在listener中处理
            ExcelListener listener = new ExcelListener();
            ExcelReader excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLS, null, listener);
            excelReader.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /* 解析监听器，
     * 每解析一行会回调invoke()方法。
     * 整个excel解析结束会执行doAfterAllAnalysed()方法
     */
    public class ExcelListener extends AnalysisEventListener {

        //自定义用于暂时存储data。
        //可以通过实例获取该值
        private List<Object> datas = new ArrayList<Object>();

        public void invoke(Object object, AnalysisContext context) {
            System.out.println("当前行：" + context.getCurrentRowNum());
            System.out.println(object);
            datas.add(object);//数据存储到list，供批量处理，或后续自己业务逻辑处理。
            doSomething(object);//根据自己业务做处理
        }

        private void doSomething(Object object) {
            //1、入库调用接口
        }

        public void doAfterAllAnalysed(AnalysisContext context) {
            // datas.clear();//解析结束销毁不用的资源
        }

        public List<Object> getDatas() {
            return datas;
        }

        public void setDatas(List<Object> datas) {
            this.datas = datas;
        }
    }

}