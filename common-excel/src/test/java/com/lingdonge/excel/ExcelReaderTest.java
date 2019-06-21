package com.lingdonge.excel;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ExcelReaderTest extends ExcelReaderAbstract {

    public static void main(String[] args) throws Exception {
        String file = "/Users/kyle/Downloads/海南企业名录.xls";

        ExcelReaderTest howto = new ExcelReaderTest();
        howto.processOneSheet(file, 1);
//        howto.process(file);
    }

    @Override
    public void optRows(int sheetIndex, int curRow, List<String> rowlist) throws SQLException {

        System.out.println(Arrays.toString(rowlist.toArray()));
        if (sheetIndex == 0 && curRow == 0) {
            // 第一行，表头部份
        } else if (curRow > 0) {

        }
    }
}
