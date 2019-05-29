package com.lindonge.core.file.fileThreads;

import com.lindonge.core.dates.DateUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 读取文件的具体线程
 */
@Slf4j
public class ReadFileThread extends Thread {

    private ReaderFileListener processPoiDataListeners;
    private String filePath;
    private long start;
    private long end;

    public ReadFileThread(ReaderFileListener processPoiDataListeners, long start, long end, String file) {
        this.setName(this.getName() + "-ReadFileThread-From-" + Long.toString(start) + "-To-" + Long.toString(end));
        this.start = start;
        this.end = end;
        this.filePath = file;
        this.processPoiDataListeners = processPoiDataListeners;
    }

    @Override
    public void run() {
        ReadFile readFile = new ReadFile();
        readFile.setReaderListener(processPoiDataListeners);
        readFile.setEncode(processPoiDataListeners.getEncode());
        //        readFile.addObserver();
        try {
            readFile.readFileByLine(filePath, start, end + 1);
        } catch (Exception e) {
            log.error("ReadFileThread发生异常", e);
        }

        log.info("线程【" + this.getName() + "】于【" + DateUtil.getNowTime() + "】任务处理完成");
    }
}
