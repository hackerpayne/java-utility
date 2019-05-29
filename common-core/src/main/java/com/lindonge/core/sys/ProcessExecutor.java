package com.lindonge.core.sys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 不阻塞的Process处理
 * process = Runtime.getRuntime().exec(new String[]{"python", FileUtil.getFile(Utils.CurrentDir, "data", "snownlp_test.py").getAbsolutePath(), Base64Util.encode(content)});
 * processExecutor = new ProcessExecutor(process);
 * processExecutor.execute();
 */
public class ProcessExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ProcessExecutor.class);

    private Process p;
    private List<String> outputList;
    private List<String> errorOutputList;

    /**
     * 构造函数
     *
     * @param p
     * @throws IOException
     */
    public ProcessExecutor(Process p) throws IOException {
        if (null == p) {
            throw new IOException("the provided Process is null");
        }
        this.p = p;
    }

    /**
     * 获取OutPut的输出列表
     *
     * @return
     */
    public List<String> getOutputList() {
        return this.outputList;
    }

    public List<String> getErrorOutputList() {
        return this.errorOutputList;
    }

    /**
     * 执行命令任务
     *
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public int execute() throws InterruptedException, IOException {
        int rs = 0;
        ProcessOutputThread outputThread = new ProcessOutputThread(this.p.getInputStream());
        ProcessOutputThread errorOutputThread = new ProcessOutputThread(this.p.getErrorStream());
        outputThread.start();
        errorOutputThread.start();
        rs = p.waitFor();
        outputThread.join();
        errorOutputThread.join();
        this.outputList = outputThread.getOutputList();
        this.errorOutputList = errorOutputThread.getOutputList();
        return rs;
    }

    /**
     * 处理输出流
     */
    class ProcessOutputThread extends Thread {
        private InputStream is;
        private List<String> outputList;

        public ProcessOutputThread(InputStream is) throws IOException {
            if (null == is) {
                throw new IOException("the provided InputStream is null");
            }
            this.is = is;
            this.outputList = new ArrayList<String>();
        }

        public List<String> getOutputList() {
            return this.outputList;
        }

        @Override
        public void run() {
            InputStreamReader ir = null;
            BufferedReader br = null;
            try {
                ir = new InputStreamReader(this.is);
                br = new BufferedReader(ir);
                String output = null;
                while (null != (output = br.readLine())) {
                    this.outputList.add(output);
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            } finally {
                try {
                    if (null != br) {
                        br.close();
                    }
                    if (null != ir) {
                        ir.close();
                    }
                    if (null != this.is) {
                        this.is.close();
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }

        }
    }
}
