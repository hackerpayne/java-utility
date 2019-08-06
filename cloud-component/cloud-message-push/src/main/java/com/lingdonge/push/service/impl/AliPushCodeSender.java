package com.lingdonge.push.service.impl;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.push.model.v20160801.PushRequest;
import com.aliyuncs.push.model.v20160801.PushResponse;
import com.aliyuncs.utils.ParameterHelper;
import com.lingdonge.push.bean.PushSendResult;
import com.lingdonge.push.configuration.properties.PushProperties;
import com.lingdonge.push.enums.PushSubmitStatus;
import com.lingdonge.push.service.PushCodeSender;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里消息推送
 */

@Slf4j
public class AliPushCodeSender implements PushCodeSender {

    @Override
    public PushSendResult messagePush(PushProperties smsSenderAccount, String alias, String notificationTitle,
                                      String msgTitle, String msgContent, String extras, Integer deviceType, Integer pushType, Date pushDate) {
        PushSendResult result = new PushSendResult();
        try {
            Map<String, String> map = advancedPush(smsSenderAccount, deviceType, pushType, alias, notificationTitle,
                    msgContent, extras, pushDate);
            result.setMessageId(map.get("messageId"));
            result.setRequestId(map.get("requestId"));
            result.setSubmitStatus(PushSubmitStatus.SUCCESS); // 提交成功
            result.setSubmitResult("");// 提交成功一般不需要记状态

        } catch (Exception e) {
            result.setSubmitStatus(PushSubmitStatus.FAIL); // 提交失败
            result.setSubmitResult(e.getMessage()); // 取出失败结果，保存提交失败的原因
        }
        return result;
    }

    /**
     * 推送高级接口
     * <p>
     * 参见文档 https://help.aliyun.com/document_detail/48089.html //
     */
    public static Map<String, String> advancedPush(PushProperties smsSenderAccount, Integer deviceType,
                                                   Integer pushType, String alias, String title, String content, String jsonObject, Date pushDate)
            throws Exception {
        PushRequest pushRequest = new PushRequest();
        // 安全性比较高的内容建议使用HTTPS
        pushRequest.setProtocol(ProtocolType.HTTPS);
        // 内容较大的请求，使用POST请求
        pushRequest.setMethod(MethodType.POST);
        // 设备类型:0-iOS,1-Android
        pushRequest.setAppKey(deviceType == 1 ? Long.valueOf(smsSenderAccount.getAli().getAndroidAppKey())
                : Long.valueOf(smsSenderAccount.getAli().getIosAppKey()));
        pushRequest.setTarget("ACCOUNT"); // 推送目标: DEVICE:按设备推送 ALIAS : 按别名推送 ACCOUNT:按帐号推送 TAG:按标签推送; ALL: 广播推送
        pushRequest.setTargetValue(alias); // 根据Target来设定，如Target=DEVICE, 则对应的值为 设备id1,设备id2.
        // 消息类型:0-message;1-notice;2-站内信
        pushRequest.setPushType(pushType == null ? "NOTICE" : pushType == 1 ? "NOTICE" : "MESSAGE"); // 消息类型 MESSAGE
        // NOTICE
        pushRequest.setDeviceType("ALL"); // 设备类型 ANDROID iOS ALL.

        // 推送配置
        pushRequest.setTitle(title); // 消息的标题
        pushRequest.setBody(deviceType == 0 ? content : jsonObject); // 消息的内容

        // 推送配置: iOS
        pushRequest.setIOSBadge(1); // iOS应用图标右上角角标
        pushRequest.setIOSSilentNotification(false);// 开启静默通知
        pushRequest.setIOSMusic("default"); // iOS通知声音
        // pushRequest.setIOSSubtitle(title);// iOS10通知副标题的内容
        pushRequest.setIOSNotificationCategory("iOS10 Notification Category");// 指定iOS10通知Category
        pushRequest.setIOSMutableContent(true);// 是否允许扩展iOS通知内容
        pushRequest.setIOSApnsEnv(smsSenderAccount.getAli().isIosApnsProduction() ? "PRODUCT" : "DEV");// iOS的通知是通过APNs中心来发送的，需要填写对应的环境信息。"DEV"
        // : 表示开发环境 "PRODUCT" :
        // 表示生产环境
        pushRequest.setIOSRemind(true); // 消息推送时设备不在线（既与移动推送的服务端的长连接通道不通），则这条推送会做为通知，通过苹果的APNs通道送达一次。注意：离线消息转通知仅适用于生产环境
        pushRequest.setIOSRemindBody("iOSRemindBody");// iOS消息转通知时使用的iOS通知内容，仅当iOSApnsEnv=PRODUCT && iOSRemind为true时有效
        pushRequest.setIOSExtParameters("{\"jsonObject\":" + jsonObject + "}"); // 通知的扩展属性(注意 : 该参数要以json
        // map的格式传入,否则会解析出错)
        // 推送配置: Android
        pushRequest.setAndroidNotifyType("NONE");// 通知的提醒方式 "VIBRATE" : 震动 "SOUND" : 声音 "BOTH" : 声音和震动 NONE : 静音
        pushRequest.setAndroidNotificationBarType(1);// 通知栏自定义样式0-100
        pushRequest.setAndroidNotificationBarPriority(1);// 通知栏自定义样式0-100
        pushRequest.setAndroidOpenType("URL"); // 点击通知后动作 "APPLICATION" : 打开应用 "ACTIVITY" : 打开AndroidActivity "URL" :
        // 打开URL "NONE" : 无跳转
        pushRequest.setAndroidOpenUrl("http://www.aliyun.com"); // Android收到推送后打开对应的url,仅当AndroidOpenType="URL"有效
        pushRequest.setAndroidActivity("com.alibaba.push2.demo.XiaoMiPushActivity"); // 设定通知打开的activity，仅当AndroidOpenType="Activity"有效
        pushRequest.setAndroidMusic("default"); // Android通知音乐
        pushRequest.setAndroidXiaoMiActivity("com.ali.demo.MiActivity");// 设置该参数后启动小米托管弹窗功能,
        // 此处指定通知点击后跳转的Activity（托管弹窗的前提条件：1. 集成小米辅助通道；2.
        // StoreOffline参数设为true）
        pushRequest.setAndroidXiaoMiNotifyTitle("Mi title");
        pushRequest.setAndroidXiaoMiNotifyBody("MiActivity Body");
        pushRequest.setAndroidExtParameters("{\"k1\":\"android\",\"k2\":\"v2\"}"); // 设定通知的扩展属性。(注意 : 该参数要以 json map
        // 的格式传入,否则会解析出错)

//        // 推送控制
        // Date pushDate = new Date(System.currentTimeMillis()); // 30秒之间的时间点,
        // 也可以设置成你指定固定时间
        // String pushTime = ParameterHelper.getISO8601Time(pushDate);
        if (pushDate != null) {
            pushRequest.setPushTime(ParameterHelper.getISO8601Time(pushDate)); // 延后推送。可选，如果不设置表示立即推送
        }
        String expireTime = ParameterHelper.getISO8601Time(new Date(System.currentTimeMillis() + 12 * 3600 * 1000)); // 12小时后消息失效,
        // 不会再发送
        pushRequest.setExpireTime(expireTime);
        pushRequest.setStoreOffline(true); // 离线消息是否保存,若保存, 在推送时候，用户即使不在线，下一次上线则会收到

        DefaultAcsClient client = clientInit(smsSenderAccount.getAli().getAccessKeyId(), smsSenderAccount.getAli().getAccessKeySecret());

        PushResponse pushResponse = client.getAcsResponse(pushRequest);
        System.out.printf("RequestId: %s, MessageID: %s\n", pushResponse.getRequestId(), pushResponse.getMessageId());
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("requestId", pushResponse.getRequestId());
        maps.put("messageId", pushResponse.getMessageId());
        return maps;
    }

    public static DefaultAcsClient clientInit(String accessKeyId, String accessKeySecret) throws Exception {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        return new DefaultAcsClient(profile);
    }

}
