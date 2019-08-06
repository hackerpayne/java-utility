package com.lingdonge.excel;

import java.sql.SQLException;
import java.util.List;

public class ExcelReaderAbstractTest extends ExcelReaderAbstract {
    @Override
    public void optRows(int sheetIndex, int curRow, List<String> rowlist) throws SQLException {
        for (int i = 0; i < rowlist.size(); i++) {
            System.out.print("'" + rowlist.get(i) + "',");
        }
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        ExcelReaderAbstractTest howto = new ExcelReaderAbstractTest();
        howto.processOneSheet("/Users/kyle/项目Project/百度贴吧合作-NC/关键词列表/50万影视动漫1102.xlsx", 1);
//      howto.process("F:/new.xlsx");
    }
}