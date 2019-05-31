package com.lingdonge.excel;

import com.lingdonge.core.collection.MapUtil;
import com.lingdonge.core.util.StringUtils;
import com.monitorjbl.xlsx.StreamingReader;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * excel 处理工具类
 */
@Slf4j
public class ExcelHelper {

    /**
     * 使用StreamingReader流式读取Excel内容
     * https://github.com/monitorjbl/excel-streaming-reader
     *
     * @param filepath
     * @param file
     * @return
     */
    public static String readExcelByFile(String filepath, File file) {
        Workbook wb;
        StringBuilder sb = new StringBuilder();
        try {
            if (filepath.endsWith(".xls")) {
                wb = WorkbookFactory.create(file);
            } else {
                wb = StreamingReader.builder()
                        .rowCacheSize(1000)    // number of rows to keep in memory (defaults to 10)
                        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                        .open(file);            // InputStream or File for XLSX file (required)
            }
            sb = readSheet(wb, sb, filepath.endsWith(".xls"));
            wb.close();
        } catch (Exception e) {
            log.error(filepath, e);
        }
        return sb.toString();
    }

    private static StringBuilder readSheet(Workbook wb, StringBuilder sb, boolean isXls) throws Exception {
        for (Sheet sheet : wb) {
            for (Row r : sheet) {
                for (Cell cell : r) {
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        sb.append(cell.getStringCellValue());
                        sb.append(" ");
                    } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                        if (isXls) {
                            DataFormatter formatter = new DataFormatter();
                            sb.append(formatter.formatCellValue(cell));
                        } else {
                            sb.append(cell.getStringCellValue());
                        }
                        sb.append(" ");
                    }
                }
            }
        }
        return sb;
    }

    /**
     * 导出大量的数据：SXSSFWorkbook可以导出海量数据到Excel里面
     * 参考：http://poi.apache.org/spreadsheet/how-to.html#sxssf
     *
     * @param filePath
     * @throws IOException
     */
    public static void exportExcel(String filePath) throws IOException {
//        XSSFWorkbook workbook1 = new XSSFWorkbook(new FileInputStream(new File(filePath)));

        Integer rowAccess = 100;//内存中保存的记录条数
//        SXSSFWorkbook workbook = new SXSSFWorkbook(workbook1, rowAceess);
        SXSSFWorkbook workbook = new SXSSFWorkbook(rowAccess);
//        sxssfWorkbook.setCompressTempFiles(true);//压缩临时文件，性能也会有损耗
//        Sheet sheet = workbook.getSheetAt(0);
        Sheet sheet = workbook.createSheet();
        for (int rownum = 0; rownum < 100000; rownum++) {
            Row row = sheet.createRow(rownum);
            for (int cellnum = 0; cellnum < 11; cellnum++) {
                if (rownum == 0) {
                    // 首行，写入标题
                    row.createCell(cellnum).setCellValue("column" + cellnum);
                } else {
                    // 数据行，写入数据
                    row.createCell(cellnum).setCellValue("column" + cellnum);
                }

//                Cell cell = row.createCell(cellnum);
//                String address = new CellReference(cell).formatAsString();
//                cell.setCellValue(address);
            }

//            if (rownum % rowAccess == 0) {//100条保存一次，手动，只有在上面的内存条数为-1时用
//                ((SXSSFSheet) sheet).flushRows();
//            }
        }
        FileOutputStream out = new FileOutputStream("workbook.xlsx");
        workbook.write(out);
        out.flush();
        out.close();
        workbook.dispose();
    }


    /**
     * 使用ExcelReaderAbstract读取Excel文件的内容
     *
     * @param fileName
     * @return
     */
    public List<Map<String, Object>> readExcel(File fileName) {
        final List<Map<String, Object>> result = new ArrayList<>();//所有结果
        final List<Object> cellValues = new ArrayList<>();//每一行的结果
        final List<String> headerList = new ArrayList<>();// 头部结果

        ExcelReaderAbstract excelReaderAbstract = new ExcelReaderAbstract() {
            @Override
            public void optRows(int sheetIndex, int curRow, List<String> rowlist) throws SQLException {
                if (curRow == 0) {
                    headerList.clear();
                    for (String row : rowlist) {
                        headerList.add(row.trim());
                    }
                } else {
                    cellValues.clear();
                    for (String row : rowlist) {
                        cellValues.add(row);
                    }
                    result.add(MapUtil.toMap(headerList, cellValues));
                }
            }
        };

        try {
            excelReaderAbstract.process(fileName.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * excel导入
     *
     * @param keys     字段名称数组，如  ["id", "name", ... ]
     * @param filePath 文件物理地址
     * @return
     */
    public static List<Map<String, Object>> importExcel(String filePath, String[] keys) throws Exception {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<String, Object>();

        if (null == keys) {
            throw new Exception("keys can not be null!");
        }

        if (!filePath.endsWith(".xls") && !filePath.endsWith(".xlsx")) {
            throw new Exception("The file is not excel document!");
        }

        Workbook workbook = null;
        try {

            workbook = createWorkbookByFile(filePath);

            // 获取第一个工作表信息
            Sheet sheet = workbook.getSheetAt(0);

            //获得数据的总行数
            int totalRowNum = sheet.getLastRowNum();

            // 获得表头
            Row rowHead = sheet.getRow(0);

            // 获得表头总列数
            int cols = rowHead.getPhysicalNumberOfCells();

            // 传入的key数组长度与表头长度不一致
            if (keys.length != cols) {
                throw new Exception("keys length does not match head row's cols!");
            }

            Row row = null;
            Cell cell = null;

            // 遍历所有行
            for (int i = 1; i <= totalRowNum; i++) {
                // 清空数据，避免遍历时读取上一次遍历数据
                row = null;
                map = new HashMap<String, Object>();

                row = sheet.getRow(i);
                if (null == row) {
                    continue;    // 若该行第一列为空，则默认认为该行就是空行
                }

                // 遍历该行所有列
                for (short j = 0; j < cols; j++) {
                    cell = row.getCell(j);
                    if (null == cell) {
                        continue;    // 为空时，下一列
                    }
                    map.put(keys[j], getCellValue(cell));
                }

                list.add(map);
            }
        } catch (Exception e) {
            throw new Exception("analysis excel exception!", e);
        } finally {
            if (workbook != null)
                workbook.close();
        }

        return list;
    }

    /**
     * 获取Cell中的值，会自动进行判断数据的类型是什么
     *
     * @param cell
     * @return
     * @throws Exception
     */
    public static Object getCellValue(Cell cell) throws Exception {

        Object value;

        // 根据poi返回的类型，做相应的get处理，旧版本使用
//                    if (Cell.CELL_TYPE_STRING == cell.getCellType()) {

        // 新版本
        if (CellType.STRING == cell.getCellTypeEnum()) {
            value = cell.getStringCellValue();
        } else if (CellType.NUMERIC == cell.getCellTypeEnum()) {
            value = cell.getNumericCellValue();

            // 由于日期类型格式也被认为是数值型，此处判断是否是日期的格式，若时，则读取为日期类型
            if (cell.getCellStyle().getDataFormat() > 0) {
                value = cell.getDateCellValue();
            }
        } else if (CellType.BOOLEAN == cell.getCellTypeEnum()) {
            value = cell.getBooleanCellValue();
        } else if (CellType.BLANK == cell.getCellTypeEnum()) {
            value = "";
        } else if (CellType.FORMULA == cell.getCellTypeEnum()) {
            value = String.valueOf(cell.getCellFormula());
        } else {
            throw new Exception("At row: %s, col: %s, can not discriminate type!");
        }

        return value;
    }

    public enum ExcelFileEnum {
        XLSX,
        XLS
    }

    /**
     * 根据文件和Excel的类型，创建一个WorkBook
     *
     * @param type
     * @return
     */
    public static Workbook createWorkBook(ExcelFileEnum type) {
        Workbook book = null;
        try {
            if (type == ExcelFileEnum.XLSX) {
                book = new XSSFWorkbook();
            } else {
                book = new HSSFWorkbook();
            }
        } catch (Exception e) {
            log.error("createWorkBook发生异常", e);
        }

        return book;
    }

    /**
     * 根据文件的名称创建对应的WorkBook
     *
     * @param filePath
     * @return
     */
    public static Workbook createWorkBook(String filePath) {
        Workbook book = null;
        try {
            if (filePath.toLowerCase().endsWith(".xlsx")) {
                book = new XSSFWorkbook();
            } else {
                book = new HSSFWorkbook();
            }
        } catch (Exception e) {
            log.error("createWorkBook发生异常", e);
        }

        return book;
    }


    /**
     * 从文件里面创建WorkBook，根据扩展名判断Excel的版本信息
     *
     * @param filePath
     * @return
     */
    public static Workbook createWorkbookByFile(String filePath) {
        Workbook book = null;
        FileInputStream fis = null;
        try {
            // 方法一
//            book = WorkbookFactory.create(new File(filePath));

            // 方法二：可以解决FileInputStream流关不掉的问题
            fis = new FileInputStream(filePath);
//            book = WorkbookFactory.create(fis);

            // 方法三： 根据文件类型读取文件
            if (filePath.endsWith(".xls")) {
//                POIFSFileSystem poifsFileSystem = new POIFSFileSystem(fis);
//                book = new HSSFWorkbook(poifsFileSystem);
                book = new HSSFWorkbook(fis);
            } else if (filePath.endsWith(".xlsx")) {
                book = new XSSFWorkbook(new BufferedInputStream(fis));
            }

        } catch (Exception e) {
            log.error("createWorkbookByFile读取文件异常", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    log.error("createWorkbookByFile读取文件异常", e);
                }
            }
        }

        return book;

    }

    /**
     * 获取表的数量
     *
     * @param filePath
     * @return
     */
    public static Integer getSheetNum(String filePath) {
        Workbook book = createWorkbookByFile(filePath);
        if (book != null)
            return book.getNumberOfSheets();
        return 0;
    }

    /**
     * @param filePath
     * @param sheetIndex
     * @return
     */
    public static String getSheetName(String filePath, Integer sheetIndex) {
        Workbook book = createWorkbookByFile(filePath);

        if (book != null)
            return book.getSheetName(sheetIndex);
        return null;
    }

    /**
     * 给Excel表设置头部的信息
     *
     * @param book
     * @param sheetName
     * @param header
     */
    public static void setWorkBookHeader(Workbook book, String sheetName, String[] header) {

        // 对每个表生成一个新的sheet,并以表名命名
        if (org.apache.commons.lang3.StringUtils.isEmpty(sheetName)) {
            sheetName = "sheet1";
        }

        Sheet sheet = book.createSheet(sheetName);

        // 设置表头的说明
        Row topRow = sheet.createRow(0);
        for (int i = 0; i < header.length; i++) {
            setCellValue(topRow.createCell(i), header[i]);
        }

    }

    /**
     * 写一行数据
     *
     * @param book     要写入的WorkBook
     * @param rowIndex 行号，第几行
     * @param listData 要写入的列表数据
     */
    public static void writeRow(Workbook book, Integer rowIndex, String[] listData) {

        Sheet sheet = book.getSheetAt(0);
        Row row = sheet.createRow(rowIndex - 1);

        for (int j = 0; j < listData.length; j++) {
            setCellValue(row.createCell(j), listData[j] + "");
        }
    }

    /**
     * 保存WorkBook到文件中
     *
     * @param book
     * @param saveFile
     * @throws Exception
     */
    public static void saveWorkBook(Workbook book, File saveFile) throws Exception {
        OutputStream os = null;
        try {
            os = new FileOutputStream(saveFile);
            book.write(os);
        } catch (Exception e) {
            log.error("writeWorkBook发生异常", e);
            throw new Exception("write excel file error!", e);
        } finally {
            if (null != os) {
                os.flush();
                os.close();
            }
        }
    }

    /**
     * excel导出
     *
     * @param fileNamePath 导出的文件名称
     * @param sheetName    导出的sheet名称
     * @param list         数据集合
     * @param titles       第一行表头
     * @param fieldNames   字段名称数组
     * @return
     * @throws Exception
     * @author yzChen
     * @date 2017年5月6日 下午3:53:47
     */
    public static <T> File export(String fileNamePath, String sheetName, List<T> list,
                                  String[] titles, String[] fieldNames) throws Exception {

        Workbook wb = createWorkBook(fileNamePath);

        // 对每个表生成一个新的sheet,并以表名命名
        if (StringUtils.isEmpty(sheetName)) {
            sheetName = "sheet1";
        }
        Sheet sheet = wb.createSheet(sheetName);

        // 设置表头的说明
        Row topRow = sheet.createRow(0);

        if (titles != null) {
            for (int i = 0; i < titles.length; i++) {
                setCellValue(topRow.createCell(i), titles[i]);
            }
        }

        String methodName = "";
        Method method = null;
        T t = null;
        Object ret = null;
        // 遍历生成数据行，通过反射获取字段的get方法
        for (int i = 0; i < list.size(); i++) {
            t = list.get(i);
            Row row = sheet.createRow(i + 1);
            Class<? extends Object> clazz = t.getClass();
            for (int j = 0; j < fieldNames.length; j++) {
                methodName = "get" + capitalize(fieldNames[j]);
                try {
                    method = clazz.getDeclaredMethod(methodName);
                } catch (java.lang.NoSuchMethodException e) {    //	不存在该方法，查看父类是否存在。此处只支持一级父类，若想支持更多，建议使用while循环
                    if (null != clazz.getSuperclass()) {
                        method = clazz.getSuperclass().getDeclaredMethod(methodName);
                    }
                }
                if (null == method) {
                    throw new Exception(clazz.getName() + " don't have menthod --> " + methodName);
                }
                ret = method.invoke(t);
                setCellValue(row.createCell(j), ret + "");
            }
        }

        File file = null;
        OutputStream os = null;
        file = new File(fileNamePath);
        try {
            os = new FileOutputStream(file);
            wb.write(os);
        } catch (Exception e) {
            throw new Exception("write excel file error!", e);
        } finally {
            if (null != os) {
                os.flush();
                os.close();
            }
        }


        return file;
    }

    /**
     * 设置字段值
     *
     * @param cell
     * @param value
     */
    private static void setCellValue(Cell cell, String value) {
//        cell.setCellType(Cell.CELL_TYPE_STRING);
        cell.setCellType(CellType.STRING);
        //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellValue(value);
    }

    /**
     * @param str
     * @return
     */
    private static String capitalize(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        final char newChar = Character.toTitleCase(firstChar);
        if (firstChar == newChar) {
            // already capitalized
            return str;
        }

        char[] newChars = new char[strLen];
        newChars[0] = newChar;
        str.getChars(1, strLen, newChars, 1);
        return String.valueOf(newChars);
    }

}