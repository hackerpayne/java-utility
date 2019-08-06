package com.lingdonge.push.service;

import com.lingdonge.push.enums.PushCodeSenderEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * push发送工厂
 */
@Slf4j
public class PushCodeSendFactory {

    /**
     * 根据平台标记，返回一个发送者
     *
     * @param senderStr
     * @return
     */
    public static PushCodeSender getPushSender(String senderStr) {
        PushCodeSenderEnum smsSender = PushCodeSenderEnum.getName(senderStr);
        return getPushSender(smsSender);
    }

    /**
     * 根据枚举取发送者实现
     *
     * @param pushSender
     * @return
     */
    public static PushCodeSender getPushSender(PushCodeSenderEnum pushSender) {
        PushCodeSender pushCodeSender = null;
        switch (pushSender) {
            case ALI:
                log.info("使用阿里推送消息");
                pushCodeSender = new AliPushCodeSender();
                break;
            case JIGUANG:
                log.debug("使用极光推送消息");
                pushCodeSender = new JiGuangCodeSender();
                break;

            default:
                break;
        }
        return pushCodeSender;
    }

}
