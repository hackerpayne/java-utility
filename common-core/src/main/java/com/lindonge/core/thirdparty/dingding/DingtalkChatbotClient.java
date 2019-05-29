package com.lindonge.core.thirdparty.dingding;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 钉钉机器人发送消息
 * https://open-doc.dingtalk.com/docs/doc.htm?spm=a219a.7629140.0.0.karFPe&treeId=257&articleId=105735&docType=1
 */
@Slf4j
public class DingtalkChatbotClient {

    private String accessToken;

    public DingtalkChatbotClient() {

    }

    public DingtalkChatbotClient(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return
     */
    private HttpClient getHttpclient() {
        return HttpClients.createDefault();
    }

    public String getUrl() {
        return "https://oapi.dingtalk.com/robot/send?access_token=" + this.accessToken;
    }

    /**
     * 发送具体的消息
     *
     * @param IDingDingMessage
     * @return
     */
    public SendResult send(IDingDingMessage IDingDingMessage) {
        SendResult sendResult = new SendResult();
        try {
            HttpClient httpclient = getHttpclient();
            HttpPost httppost = new HttpPost(getUrl());
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");
            StringEntity se = new StringEntity(IDingDingMessage.toJsonString(), "utf-8");
            httppost.setEntity(se);
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject obj = JSONObject.parseObject(result);
                Integer errcode = obj.getInteger("errcode");
                sendResult.setErrorCode(errcode);
                sendResult.setSuccess(errcode.equals(0));
                sendResult.setErrorMsg(obj.getString("errmsg"));
            }
            return sendResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sendResult;
    }

    public static void main(String[] args) {
        DingtalkChatbotClient dingtalkChatbotClient = new DingtalkChatbotClient("a2868f48bc33573fffaa82f8650db310af81ed218430183fecd2d0fcc3d7caf0");
        TextDingMessage textDingMessage = new TextDingMessage("test");
        textDingMessage.setAtAll(false);
//        textDingMessage.setAtMobiles(List.of("18515490065"));
        dingtalkChatbotClient.send(textDingMessage);
    }

}