package com.lingdonge.core.sys;

import com.lingdonge.core.util.StringUtils;
import com.lingdonge.core.util.Utils;
import com.lingdonge.core.file.InputStreamRunnable;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 进程管理执行类
 * Created by Kyle on 16/10/12.
 */
@Slf4j
public class ProcessHelper {

    private static String currentPlatform = null;

    /**
     * 构造函数
     */
    public ProcessHelper() {
        Properties props = System.getProperties(); // 获得系统属性集
        currentPlatform = props.getProperty("os.name"); // 操作系统名称
    }

    /**
     * 关闭指定的进程
     *
     * @param processName
     * @throws IOException
     */
    public static void killProcess(String processName) throws IOException {

        // 判断系统类别，加载不同的Driver
        if (currentPlatform.toLowerCase().contains("win")) {

            excuteCMD("tskill " + processName);
            //runTime.exec("TASKKILL /F /IM "+processName);

        } else if (currentPlatform.toLowerCase().contains("linux")) {

            excuteCMD("killall -9 " + processName);
        } else {
            log.error("The [" + currentPlatform + "] is not supported for this automation frame,please change the OS(Windows,MAC or LINUX)");
        }
    }

    /**
     * 执行CMD命令
     *
     * @param command 执行CMD命令
     * @return
     */
    public static String excuteCMD(String command) {
        Runtime rt = Runtime.getRuntime();

        try {

            String osName = System.getProperty("os.name");

            if (osName.equals("Windows NT")) {
                command = "cmd.exe /C " + command;
            } else if (osName.equals("Windows 95")) {
                command = "command.exe /C " + command;
            }
//            command = command.replaceAll(" ", "\" \"");//合并命令中有空格的地方

            Process proc = rt.exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = stdInput.readLine()) != null) {
                sb.append(line).append(Utils.LineSeparator);
            }

            return sb.toString().trim();

        } catch (IOException e) {
            log.error("excuteCMD", e);
            return null;
        }

    }

    /**
     * @param cmd
     * @return
     */
    public static String excuteCMDMultiThread(String[] cmd) {
        return excuteCMDMultiThread(cmd, "utf-8");
    }

    /**
     * 执行外部程序,并获取标准输出
     *
     * @param cmd
     * @param encoding
     * @return
     */
    public static String excuteCMDMultiThread(String[] cmd, String encoding) {
        BufferedReader bReader = null;
        InputStreamReader sReader = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);

            // 为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞
            Thread t = new Thread(new InputStreamRunnable(p.getErrorStream(), "ErrorStream"));
            t.start();

            // 标准输出流、就在当前方法中读取
            BufferedInputStream bis = new BufferedInputStream(p.getInputStream());

            if (StringUtils.isNotEmpty(encoding)) {
                sReader = new InputStreamReader(bis, encoding);//设置编码方式
            } else {
                sReader = new InputStreamReader(bis, "GBK");
            }

            bReader = new BufferedReader(sReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bReader.readLine()) != null) {
                sb.append(line).append(Utils.LineSeparator);
            }

            bReader.close();
            p.destroy();
            return sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        } finally {
        }
    }


}
