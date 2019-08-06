package com.lingdonge.push.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.http.ProtocolType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.push.model.v20160801.*;
import com.aliyuncs.utils.ParameterHelper;
import com.lingdonge.push.configuration.properties.AliPushProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里消息推送
 */

public class AliMessagePushUtil {

    private AliPushProperties aliPushProperties;

    public AliMessagePushUtil(AliPushProperties aliPushProperties) {
        this.aliPushProperties = aliPushProperties;
    }

    public DefaultAcsClient getClient() {
        IClientProfile profile = DefaultProfile.getProfile(aliPushProperties.getRegionId(), aliPushProperties.getAccessKeyId(), aliPushProperties.getAccessKeySecret());
        return new DefaultAcsClient(profile);
    }

    /**
     * 推送高级接口
     * <p>
     * 参见文档 https://help.aliyun.com/document_detail/48089.html //
     */
    public Map<String, String> advancedPush(Integer deviceType, Integer pushType, String account, String title, String jsonObject, String content, Date pushDate) throws Exception {
        PushRequest pushRequest = new PushRequest();
        // 安全性比较高的内容建议使用HTTPS
        pushRequest.setProtocol(ProtocolType.HTTPS);
        // 内容较大的请求，使用POST请求
        pushRequest.setMethod(MethodType.POST);
        //设备类型:0-iOS,1-Android
        pushRequest.setAppKey(deviceType == 1 ? Long.valueOf(aliPushProperties.getAndroidAppKey()) : Long.valueOf(aliPushProperties.getIosAppKey()));
        pushRequest.setTarget("ACCOUNT"); // 推送目标: DEVICE:按设备推送 ALIAS : 按别名推送 ACCOUNT:按帐号推送 TAG:按标签推送; ALL: 广播推送
        pushRequest.setTargetValue(account); // 根据Target来设定，如Target=DEVICE, 则对应的值为 设备id1,设备id2.
        //消息类型:0-message;1-notice;2-站内信
        pushRequest.setPushType(pushType == null ? "NOTICE" : pushType == 1 ? "NOTICE" : "MESSAGE"); // 消息类型 MESSAGE NOTICE
        pushRequest.setDeviceType("ALL"); // 设备类型 ANDROID iOS ALL.

        // 推送配置
        pushRequest.setTitle(title); // 消息的标题
        pushRequest.setBody(deviceType == 0 ? content : jsonObject); // 消息的内容

        // 推送配置: iOS
        pushRequest.setIOSBadge(1); // iOS应用图标右上角角标
        pushRequest.setIOSSilentNotification(false);// 开启静默通知
        pushRequest.setIOSMusic("default"); // iOS通知声音
        //pushRequest.setIOSSubtitle(title);// iOS10通知副标题的内容
        pushRequest.setIOSNotificationCategory("iOS10 Notification Category");// 指定iOS10通知Category
        pushRequest.setIOSMutableContent(true);// 是否允许扩展iOS通知内容
        pushRequest.setIOSApnsEnv("DEV");// iOS的通知是通过APNs中心来发送的，需要填写对应的环境信息。"DEV" : 表示开发环境 "PRODUCT" : 表示生产环境
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
        //Date pushDate = new Date(System.currentTimeMillis()); // 30秒之间的时间点, 也可以设置成你指定固定时间
        //String pushTime = ParameterHelper.getISO8601Time(pushDate);
        if (pushDate != null) {
            pushRequest.setPushTime(ParameterHelper.getISO8601Time(pushDate)); // 延后推送。可选，如果不设置表示立即推送
        }
        String expireTime = ParameterHelper.getISO8601Time(new Date(System.currentTimeMillis() + 12 * 3600 * 1000)); // 12小时后消息失效,
        // 不会再发送
        pushRequest.setExpireTime(expireTime);
        pushRequest.setStoreOffline(true); // 离线消息是否保存,若保存, 在推送时候，用户即使不在线，下一次上线则会收到

        PushResponse pushResponse = getClient().getAcsResponse(pushRequest);
        System.out.printf("RequestId: %s, MessageID: %s\n", pushResponse.getRequestId(), pushResponse.getMessageId());
        Map<String, String> maps = new HashMap<String, String>();
        maps.put("requestId", pushResponse.getRequestId());
        maps.put("messageId", pushResponse.getMessageId());
        return maps;
    }

    /**
     * 推送消息给android
     * <p>
     * 参见文档 https://help.aliyun.com/document_detail/48085.html
     */
    public void pushMessageToAndroid() throws Exception {

        PushMessageToAndroidRequest androidRequest = new PushMessageToAndroidRequest();
        // 安全性比较高的内容建议使用HTTPS
        androidRequest.setProtocol(ProtocolType.HTTPS);
        // 内容较大的请求，使用POST请求
        androidRequest.setMethod(MethodType.POST);
        androidRequest.setAppKey(Long.valueOf(aliPushProperties.getAndroidAppKey()));
        androidRequest.setTarget("ALL");
        androidRequest.setTargetValue("ALL");
        androidRequest.setTitle("66666666666");
        androidRequest.setBody("6");
        PushMessageToAndroidResponse pushMessageToAndroidResponse = getClient().getAcsResponse(androidRequest);
        System.out.printf("RequestId: %s, MessageId: %s\n", pushMessageToAndroidResponse.getRequestId(),
                pushMessageToAndroidResponse.getMessageId());

    }

    /**
     * 推送通知给android
     * <p>
     * 参见文档 https://help.aliyun.com/document_detail/48087.html
     */
    public void pushNoticeToAndroid() throws Exception {

        PushNoticeToAndroidRequest androidRequest = new PushNoticeToAndroidRequest();
        // 安全性比较高的内容建议使用HTTPS
        androidRequest.setProtocol(ProtocolType.HTTPS);
        // 内容较大的请求，使用POST请求
        androidRequest.setMethod(MethodType.POST);
        androidRequest.setAppKey(Long.valueOf(aliPushProperties.getAndroidAppKey()));
        androidRequest.setTarget("TAG");
        androidRequest.setTargetValue("tag1");
        androidRequest.setTitle("title");
        androidRequest.setBody("Body");
        androidRequest.setExtParameters("{\"k1\":\"v1\"}");

        PushNoticeToAndroidResponse pushNoticeToAndroidResponse = getClient().getAcsResponse(androidRequest);
        System.out.printf("RequestId: %s, MessageId: %s\n", pushNoticeToAndroidResponse.getRequestId(),
                pushNoticeToAndroidResponse.getMessageId());

    }

    /**
     * 推送消息给iOS
     * <p>
     * 参见文档 https://help.aliyun.com/document_detail/48086.html
     */
    public void pushMessageToIOS() throws Exception {
        PushMessageToiOSRequest iOSRequest = new PushMessageToiOSRequest();
        // 安全性比较高的内容建议使用HTTPS
        iOSRequest.setProtocol(ProtocolType.HTTPS);
        // 内容较大的请求，使用POST请求
        iOSRequest.setMethod(MethodType.POST);
        iOSRequest.setAppKey(Long.valueOf(aliPushProperties.getIosAppKey()));
        iOSRequest.setTarget("DEVICE");
        iOSRequest.setTargetValue("");
        iOSRequest.setTitle("title");
        iOSRequest.setBody("body");

        PushMessageToiOSResponse pushMessageToiOSResponse = getClient().getAcsResponse(iOSRequest);
        System.out.printf("RequestId: %s, MessageId: %s\n", pushMessageToiOSResponse.getRequestId(),
                pushMessageToiOSResponse.getMessageId());
    }

    /**
     * 推送通知给iOS
     * <p>
     * 参见文档 https://help.aliyun.com/document_detail/48088.html
     */
    public void pushNoticeToIOS_toAll() throws Exception {

        PushNoticeToiOSRequest iOSRequest = new PushNoticeToiOSRequest();
        // 安全性比较高的内容建议使用HTTPS
        iOSRequest.setProtocol(ProtocolType.HTTPS);
        // 内容较大的请求，使用POST请求
        iOSRequest.setMethod(MethodType.POST);
        iOSRequest.setAppKey(Long.valueOf(aliPushProperties.getIosAppKey()));
        // iOS的通知是通过APNS中心来发送的，需要填写对应的环境信息. DEV :表示开发环境, PRODUCT: 表示生产环境
        iOSRequest.setApnsEnv("PRODUCT");
        iOSRequest.setTarget("DEVICE");
        iOSRequest.setTargetValue("e24155d9f3db4e3791e5444d737c81db");
        // iOSRequest.setTitle("eewwewe");
        iOSRequest.setBody("Body");
        iOSRequest.setExtParameters("{\"k1\":\"v1\",\"k2\":\"v2\"}");

        PushNoticeToiOSResponse pushNoticeToiOSResponse = getClient().getAcsResponse(iOSRequest);
        System.out.printf("RequestId: %s, MessageId: %s\n", pushNoticeToiOSResponse.getRequestId(),
                pushNoticeToiOSResponse.getMessageId());
    }

    /**
     * 取消定时推送
     * <p>
     * //
     */
    public void cancelPush() throws Exception {
        CancelPushRequest request = new CancelPushRequest();
        request.setAppKey(Long.valueOf(aliPushProperties.getIosAppKey()));
        request.setMessageId(510456L);
        CancelPushResponse response = getClient().getAcsResponse(request);
        System.out.println(response.getRequestId());

    }

}
