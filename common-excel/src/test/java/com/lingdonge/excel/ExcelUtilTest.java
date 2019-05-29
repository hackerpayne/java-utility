package com.lingdonge.excel;

import com.kyle.utility.file.FileUtil;
import com.kyle.utility.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class ExcelUtilTest {

    public void testReadFile() {

    }

    @Test
    public void testRead() throws Exception {
        String file = "/Users/kyle/Downloads/名录/上海地区，/上海地区/上海/上海000/上海000().xls";

        String[] title = {"公司名称", "电话号码", "手机号码"};
        List<Map<String, Object>> listLines = ExcelHelper.importExcel(file, title);

        System.out.println(listLines.size());
    }

    public static void main(String[] args) throws Exception {
//        Workbook workbook = ExcelHelper.createWorkbookByFile(FileUtil.getFile(Utils.CurrentDir,"logs","50万影视动漫1102.xlsx").getAbsolutePath());
        Workbook workbook = ExcelHelper.createWorkbookByFile(FileUtil.getFile(Utils.CurrentDir, "logs", "考勤枚举案例.xlsx").getAbsolutePath());
//        Workbook workbook = ExcelHelper.createWorkbookByFile("/Users/kyle/项目Project/百度贴吧合作-NC/关键词列表/nc关键词1030.xlsx");

        System.out.println(workbook);

        Workbook book = ExcelHelper.createWorkBook(ExcelHelper.ExcelFileEnum.XLSX);

        ExcelHelper.setWorkBookHeader(book, "", new String[]{"Test1", "Test2", "Tese3"});

        ExcelHelper.writeRow(book, 2, new String[]{"2", "Test2", "Tese3"});
        ExcelHelper.writeRow(book, 3, new String[]{"3", "Test2", "Tese3"});
        ExcelHelper.writeRow(book, 4, new String[]{"4", "Test2", "Tese3"});
        ExcelHelper.writeRow(book, 5, new String[]{"5", "Test2", "Tese3"});
        ExcelHelper.writeRow(book, 6, new String[]{"6", "Test2", "Tese3"});

        ExcelHelper.saveWorkBook(book, FileUtils.getFile("conf", "multithreading.xlsx"));

        System.out.println("multithreading Saved");
//
//        String[] titles = new String[]{"Id", "Brand"};
//        String[] fieldNames = new String[]{"id", "brand"};
//        List<Order> expList = new ArrayList<Order>();
//        Order order = new Order();
//        order.setId(1L);
//        order.setBrand("第三方手动阀");
//        expList.add(order);
//        order = new Order();
//        order.setId(2L);
//        order.setBrand("scsdsad");
//        expList.add(order);
//
//        String fileNamePath = "E:/order.xls";
//        try {
//            ExcelHelper.export(fileNamePath, "订单", expList, titles, fieldNames);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        System.out.println("------------------------------------------");
//
//        String filePath = "E:/order.xls";
//        String[] keys = new String[]{"id", "brand"};
//
//        List<Map<String, Object>> impList;
//        try {
//            impList = ExcelHelper.imp(filePath, keys);
//
//            for (Map<String, Object> map : impList) {
//                System.out.println(map.get("brand"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}