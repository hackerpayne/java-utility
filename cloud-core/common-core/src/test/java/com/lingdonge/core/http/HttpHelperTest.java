package com.lingdonge.core.http;

import com.lingdonge.core.captcha.CaptchaFrame;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.awt.image.BufferedImage;

/**
 * Created by kyle on 17/4/12.
 */
@Slf4j
public class HttpHelperTest {

    private HttpHelper http;

    @Test
    public void testGetImage() throws Exception {
        http = new HttpHelper();
        BufferedImage img = http.getImage("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/logo_white_fe6da1ec.png", "");

//        ImageIO.write(img, "png", new File("result.png"));

        //显示输入框，输入用户验证码
        String input = new CaptchaFrame(img).getUserInput();
        log.info("验证码为：" + input);
    }

}