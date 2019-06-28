package com.lingdonge.excel;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * https://github.com/alibaba/easyexcel
 */
public class EasyExcelUtil {

    /**
     * 导出Excel
     *
     * @param request
     * @param response
     * @param fileName
     * @param sheetName
     * @param data
     * @throws IOException
     */
    public void exportExcel(HttpServletRequest request, HttpServletResponse response, String fileName, String sheetName, List<List<String>> data) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        response.setContentType("multipart/form-data");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);

        Sheet sheet1 = new Sheet(1, 0);
        sheet1.setSheetName(sheetName);

        writer.write0(data, sheet1);
        writer.finish();
        out.flush();
    }


}
