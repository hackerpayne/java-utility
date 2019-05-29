package com.lingdonge.excel;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * XSSF and SAX (Event API)
 * 提供行级操作方法 optRows，需要extends进行使用
 * Excel超大文件读取方法：参考：
 * http://poi.apache.org/spreadsheet/how-to.html#XSSF+and+SAX+%28Event+API%29
 * http://www.iteye.com/topic/624969
 * http://thinkgem.iteye.com/blog/2150940
 * http://www.cnblogs.com/scw2901/p/4378424.html
 * http://blog.csdn.net/lishengbo/article/details/40711769
 * http://blog.csdn.net/hero06206/article/details/61916101
 * Test下面的2个示例
 */
public abstract class ExcelReaderAbstract extends DefaultHandler {

    private SharedStringsTable sst;
    private String lastContents;
    private boolean nextIsString;

    public int getSheetCount() {
        return sheetCount;
    }

    public void setSheetCount(int sheetCount) {
        this.sheetCount = sheetCount;
    }

    private int sheetCount = -1;

    private int sheetIndex = -1;
    private List<String> rowlist = new ArrayList<String>();
    private int curRow = 0;
    private int curCol = 0;

    //excel记录行操作方法，以行索引和行元素列表为参数，对一行元素进行操作，元素为String类型
//  public abstract void optRows(int curRow, List<String> rowlist) throws SQLException ;

    /**
     * excel记录行操作方法，以sheet索引，行索引和行元素列表为参数，对sheet的一行元素进行操作，元素为String类型
     *
     * @param sheetIndex
     * @param curRow
     * @param rowlist
     * @throws SQLException
     */
    public abstract void optRows(int sheetIndex, int curRow, List<String> rowlist) throws SQLException;

    /**
     * 只遍历一个sheet，其中sheetId为要遍历的sheet索引，从1开始，1-3
     *
     * @param filename 文件名
     * @param sheetId
     * @throws Exception
     */
    public void processOneSheet(String filename, int sheetId) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);

        // rId2 found by processing the Workbook
        // 根据 rId# 或 rSheet# 查找sheet
        InputStream sheet2 = r.getSheet("rId" + sheetId);
        sheetIndex++;
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }

    /**
     * 遍历 excel 文件所有Sheet进行处理
     *
     * @param filename
     * @throws Exception
     */
    public void process(String filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();

        XMLReader parser = fetchSheetParser(sst);

        Iterator<InputStream> sheets = r.getSheetsData();
        while (sheets.hasNext()) {
            curRow = 0;
            sheetIndex++;
            InputStream sheet = sheets.next();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            sheet.close();
        }
    }

    /**
     * 取出Sheet的数量
     *
     * @param filename
     * @throws Exception
     */
    public void processSheetNum(File filename) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
//        SharedStringsTable sst = r.getSharedStringsTable();

//        XMLReader parser = fetchSheetParser(sst);

//        Iterator<InputStream> sheets = r.getSheetsData();

        sheetCount = 0;

        Iterator<InputStream> sheets = r.getSheetsData();

        while (sheets.hasNext()) {
            InputStream inputStream = sheets.next();
            inputStream.close();
            sheetCount++;
        }

//        System.out.println(sheetCount);
    }

    /**
     * @param sst
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     */
    public XMLReader fetchSheetParser(SharedStringsTable sst)
            throws SAXException, ParserConfigurationException {

        // 方法一：
//        XMLReader parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");

        // 方法二：
        SAXParserFactory m_parserFactory = null;
        // If unable to create an instance, let's try to use
        // the XMLReader from JAXP
        m_parserFactory = SAXParserFactory.newInstance();
        m_parserFactory.setNamespaceAware(true);

        XMLReader parser = m_parserFactory.newSAXParser().getXMLReader();

        this.sst = sst;
        parser.setContentHandler(this);
        return parser;
    }

    /**
     * @param uri
     * @param localName
     * @param name
     * @param attributes
     * @throws SAXException
     */
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
        // c => 单元格
        if (name.equals("c")) {
            // 如果下一个元素是 SST 的索引，则将nextIsString标记为true
            String cellType = attributes.getValue("t");
            if (cellType != null && cellType.equals("s")) {
                nextIsString = true;
            } else {
                nextIsString = false;
            }
        }
        // 置空
        lastContents = "";
    }

    /**
     * @param uri
     * @param localName
     * @param name
     * @throws SAXException
     */
    public void endElement(String uri, String localName, String name) throws SAXException {
        // 根据SST的索引值的到单元格的真正要存储的字符串
        // 这时characters()方法可能会被调用多次
        if (nextIsString) {
            try {
                int idx = Integer.parseInt(lastContents);
                lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
            } catch (Exception e) {

            }
        }

        // v => 单元格的值，如果单元格是字符串则v标签的值为该字符串在SST中的索引
        // 将单元格内容加入rowlist中，在这之前先去掉字符串前后的空白符
        if (name.equals("v")) {
            String value = lastContents.trim();
            value = value.equals("") ? " " : value;
            rowlist.add(curCol, value);
            curCol++;
        } else {
            //如果标签名称为 row ，这说明已到行尾，调用 optRows() 方法
            if (name.equals("row")) {
                try {
                    optRows(sheetIndex, curRow, rowlist);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                rowlist.clear();
                curRow++;
                curCol = 0;
            }
        }
    }

    /**
     * 得到单元格内容的值
     *
     * @param ch
     * @param start
     * @param length
     * @throws SAXException
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
        //得到单元格内容的值
        lastContents += new String(ch, start, length);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) throws Exception {

        String file = "E:/导入测试数据.xlsx";

        ExcelReaderAbstract reader = new ExcelReaderAbstract() {
            @Override
            public void optRows(int sheetIndex, int curRow, List<String> rowList) {

                System.out.println("Sheet:" + sheetIndex + ", Row:" + curRow + ", Data:" + rowList);

            }
        };
        reader.process(file);

    }
}