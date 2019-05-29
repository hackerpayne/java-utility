package com.lingdonge.spring.mail;

import com.lingdonge.spring.mail.impl.MailServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Autowired
    private MailServiceImpl mailService;

    private String to = "hackerpayne@qq.com";


    @Test
    public void sendTextMail() {
        mailService.sendTextEmail(to, "this is a text mail Test", "hahahha");
    }

    @Test
    public void sendHtmlMail() {
        mailService.sendHtmlEmail(to, "this is a html mail Test", "<p> Test</p><br/><strong>hElllo</strong>");
    }

    @Test
    public void sendAttachMail() {
        String file = "/Users/kyle/Downloads/WI-logo.jpg";
        mailService.sendAttachmentsMail(to, "this is a attach multithreading", "hahahah", new String[]{file});
    }

    @Test
    public void sendAttachHtmlMail() {
        String file = "/Users/kyle/Downloads/WI-logo.jpg";
        mailService.sendAttachmentsHtmlMail(to, "this is a attach multithreading", "<p> Test</p><br/><strong>hElllo</strong>", new String[]{file});
    }
}
