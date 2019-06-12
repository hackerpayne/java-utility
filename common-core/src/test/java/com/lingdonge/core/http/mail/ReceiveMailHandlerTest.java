package com.lingdonge.core.http.mail;

import com.lingdonge.core.http.mail.entity.MailEntity;
import com.lingdonge.core.http.mail.entity.RecieveAccount;
import org.junit.Test;

import java.util.List;

import static com.lingdonge.core.http.mail.entity.EnumRecieveType.POP3;

public class ReceiveMailHandlerTest {


    @Test
    public void testSendMail() {
        ReceiveMailHandler receiveMailHandler = new ReceiveMailHandler();
        RecieveAccount recieveAccount = new RecieveAccount();

        // Sina邮箱
//        recieveAccount.setHost("pop.sina.com");
//        recieveAccount.setPort(995);
//        recieveAccount.setUserName("teste@sina.com");
//        recieveAccount.setPassword("teste");
//        recieveAccount.setRecieveType(EnumRecieveType.POP3);
//        recieveAccount.setEnableSSL(true);

        recieveAccount.setHost("pop.qq.com");
        recieveAccount.setPort(995);
        recieveAccount.setUserName("test@qq.com");
        recieveAccount.setPassword("tse");
        recieveAccount.setRecieveType(POP3);
        recieveAccount.setEnableSSL(true);

        List<MailEntity> listUnreadMails = receiveMailHandler.receiveMail(recieveAccount, 10);

        System.out.println(listUnreadMails.size());
    }

}