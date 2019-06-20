package com.lingdonge.core.thirdparty.dingding;

import com.sun.tools.javac.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

public class DingtalkChatbotClientTest {

    @Test
    public void alertWarninx() {
        DingtalkChatbotClient dingtalkChatbotClient = new DingtalkChatbotClient("a2868f48bc33573fffaa82f8650db310af81ed218430183fecd2d0fcc3d7caf0");
        TextDingMessage textDingMessage = new TextDingMessage("test");
        textDingMessage.setAtAll(false);
        textDingMessage.setAtMobiles(List.of("18515490065"));
        dingtalkChatbotClient.send(textDingMessage);
    }

}