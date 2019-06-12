package com.lingdonge.http.webmagic.pipeline;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.lingdonge.core.file.FileUtil;
import com.lingdonge.core.util.Utils;
import com.lingdonge.http.webmagic.ResultItems;
import com.lingdonge.http.webmagic.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * CSV的保存Pipeline，适合小量的CSV的保存
 */
@Slf4j
public class CSVFilePipeline implements Pipeline {

    List<String> listHeaderKeys = null;
    String saveFilePath = null;
    CSVPrinter printer;
    Writer writer;


    /**
     * 构造函数
     *
     * @param headers  头部，必须是Key/value格式的头部，Key是字段名称，Value是字段中文值
     * @param savePath
     */
    public CSVFilePipeline(Map<String, String> headers, String savePath) {

        this.saveFilePath = FileUtil.file(Utils.CurrentDir, "data", savePath).getAbsolutePath();

        try {
            CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator("\n");
            writer = new FileWriter(saveFilePath);
            printer = new CSVPrinter(writer, format);

            // 把头部打印进来，另把字段进到列表里面
            List<String> listHeaders = Lists.newArrayList();
            this.listHeaderKeys = Lists.newArrayList();
            for (Map.Entry<String, String> map : headers.entrySet()) {
                listHeaders.add(map.getValue());
                listHeaderKeys.add(map.getKey());
            }
            printer.printRecord(listHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存数据
     *
     * @param resultItems
     * @param task
     */
    @Override
    public void process(ResultItems resultItems, Task task) {

        String url = resultItems.getRequest().getUrl();

        List<Map<String, ?>> listDatas = resultItems.get("dataList");
        if (listDatas == null) {
            listDatas = Lists.newArrayList();
        }

        Map<String, ?> itemData = resultItems.get("data");
        listDatas.add(itemData);

        if (listDatas.size() <= 0) {
            log.error(StrUtil.format("CompanyDataPipeline保存URL：【{}】到数据库出错，表名不能为空，数据不能为空！！！", url));
            return;//没数据直接跳出了，不要进去处理了
        }

        // 遍历数据进行插入
        List<Object> dataRecord = Lists.newArrayList();

        for (Map<String, ?> model : listDatas) {

            if (model == null) {
                continue;
            }

            dataRecord.clear();
            for (String key : listHeaderKeys) {
                dataRecord.add(model.get(key));
            }

            try {
                printer.printRecord(dataRecord);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

        }

        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
