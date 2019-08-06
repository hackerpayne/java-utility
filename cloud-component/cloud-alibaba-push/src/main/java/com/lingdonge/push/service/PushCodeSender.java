package com.lingdonge.push.service;

import com.lingdonge.push.bean.PushSendResult;
import com.lingdonge.push.bean.PushSenderAccount;

import java.util.Date;

public interface PushCodeSender {

    /**
     * push消息
     *
     * @param pushSenderAccount 推送账号
     * @param alias             发送对象(极光用别名，阿里用账号),多个用逗号隔开
     * @param notificationTitle 通知标题
     * @param msgTitle          消息标题极光用
     * @param msgContent        发送内容
     * @param extras            扩展字段
     * @param deviceType        设备类型(0:ios,1:android)
     * @param pushType          推送类型(0:消息,1:通知) ios用消息,android用通知
     * @param pushDate          定时发送
     * @return
     */
    PushSendResult messagePush(PushSenderAccount pushSenderAccount, String alias, String notificationTitle, String msgTitle, String msgContent, String extras, Integer deviceType, Integer pushType, Date pushDate);

}
