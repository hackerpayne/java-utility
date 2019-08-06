package com.lingdonge.spring.token;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * CSRF防止重复提交的Token类
 * 使用单例模式
 * 使用时：
 * String token = TokenProccessorUtil.getInstance().makeToken(); //创建令牌
 * request.getSession().setAttribute("token", token); //在服务器使用session保存token(令牌)
 * <p>
 * 页面上：
 * <input type="hidden" name="token" value="<%=session.getAttribute("token") %>"/>
 * <input type="hidden" name="token" value="${token}">
 * <p>
 * 校验时：
 * String clientToken = request.getParameter("formToken");
 * String serverToken = (String) request.getSession().getAttribute("formToken");
 * 如果不一致就是伪造的。最后需要删除掉
 * request.getSession().removeAttribute("formToken");
 */
@Slf4j
public class CsrfTokenUtil {

    HttpServletRequest request = null;

    private CsrfTokenUtil() {
    }

    private static final CsrfTokenUtil instance = new CsrfTokenUtil();

    /**
     * 返回类的对象
     *
     * @return
     */
    public static CsrfTokenUtil getInstance() {
        return instance;
    }

    /**
     * 生成Token但是会生成一些非法 字符所以直接用时间
     * Token：Nv6RRuGEVvmGjB+jimI/gw==
     *
     * @return
     */
    public String makeToken(String userId) {  //checkException
        //  7346734837483  834u938493493849384  43434384
        String token = (System.currentTimeMillis() + new Random().nextInt(999999999)) + "" + (StringUtils.isNotEmpty(userId) ? userId : "");
        //String token = java.token.UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        //数据指纹   128位长   16个字节  md5
        try {
            // MessageDigest md = MessageDigest.getInstance("md5");
            // byte md5[] =  md.digest(token.getBytes());
            //base64编码--任意二进制编码明文字符   adfsdfsdfsf
            // BASE64Encoder encoder = new BASE64Encoder();
            // return encoder.encode(md5);
            return token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断是否重复提交，针对某些用户点击过快
     *
     * @param client_token 客户端提交的TOken
     * @param server_token 服务端的TOken
     * @return
     */
    public boolean isRepeatSubmit(String client_token, String server_token) {
        //1、如果用户提交的表单数据中没有token，则用户是重复提交了表单
        if (client_token == null) {
            return true;
        }
        //取出存储在Session中的token
        //String server_token = (String) request.getSession().getAttribute("token");
        //2、如果当前用户的Session中不存在Token(令牌)，则用户是重复提交了表单
        if (server_token == null) {
            return true;
        }
        //3、存储在Session中的Token(令牌)与表单提交的Token(令牌)不同，则用户是重复提交了表单
        if (!client_token.equals(server_token)) {
            return true;
        }

        return false;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 9; i++) {
            System.out.println(CsrfTokenUtil.getInstance().makeToken(""));
        }
    }

}
