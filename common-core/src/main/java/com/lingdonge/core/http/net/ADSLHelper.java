package com.lingdonge.core.http.net;

import com.lingdonge.core.sys.ProcessHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * 拨号：语法： rasdial  连接名称 username password
 * 断网：实例： rasdial 宽带  /disconnect
 * Created by Kyle on 16/12/7.
 */
@Slf4j
public class ADSLHelper {

    /**
     * 断开网络并重新进行拨号
     *
     * @param adslTitle  拨号名称
     * @param adslName   宽带账号
     * @param adslPass   宽带密码
     * @param sleepTimes 断开后的停留时间
     */
    public static void ChangeIp(String adslTitle, String adslName, String adslPass, int sleepTimes) {

        try {
            cutAdsl(adslTitle);
            Thread.sleep(sleepTimes * 1000);
            connAdsl(adslTitle, adslName, adslPass);
        } catch (InterruptedException e) {
            log.error("ChangeIp Trigger Error", e);
        } catch (Exception e) {
            log.error("ChangeIp Trigger Error", e);
        }
    }

    /**
     * 连接ADSL
     */
    public static boolean connAdsl(String adslTitle, String adslName, String adslPass) throws Exception {
        log.info("正在建立连接.");
        String adslCmd = "rasdial " + adslTitle + " " + adslName + " "
                + adslPass;
        String tempCmd = ProcessHelper.excuteCMD(adslCmd);
        // 判断是否连接成功
        if (tempCmd.indexOf("已连接") > 0) {
            log.info("已成功建立连接.");
            return true;
        } else {
            log.info(tempCmd);
            log.info("建立连接失败");
            return false;
        }
    }

    /**
     * 断开ADSL
     */
    public static boolean cutAdsl(String adslTitle) throws Exception {
        String cutAdsl = "rasdial " + adslTitle + " /disconnect";
        String result = ProcessHelper.excuteCMD(cutAdsl);

        if (result.indexOf("没有连接") != -1) {
            log.info(adslTitle + "连接不存在!");
            return false;
        } else {
            log.info("连接已断开");
            return true;
        }
    }

    public static void main(String[] args) throws Exception {
        connAdsl("宽带", "hzhz**********", "******");
        Thread.sleep(1000);
        cutAdsl("宽带");
        Thread.sleep(1000);
        //再连，分配一个新的IP
        connAdsl("宽带", "hzhz**********", "******");
    }
}
