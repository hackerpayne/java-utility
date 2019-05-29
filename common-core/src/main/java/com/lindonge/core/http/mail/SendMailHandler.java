package com.lindonge.core.http.mail;

import com.lindonge.core.http.mail.entity.MailEntity;
import com.lindonge.core.http.mail.entity.UserAuthentication;
import com.lindonge.core.file.PropertiesUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;


/**
 * 发送邮件的实现类
 * http://blog.csdn.net/lanjian056/article/details/52711303
 */
public class SendMailHandler {

    private static Properties properties = System.getProperties();
    Session sendMailSession = null;
    UserAuthentication authenticator = null;
    Transport trans = null;

    private void connect() throws Exception {
        //读取配置文件
        Properties props = PropertiesUtils.getProperties("mail.properties");
        InputStream ins;
        //给properties赋值
        properties.put("mail.smtp.host", props.getProperty("MAIL_SMTP_HOST"));
        properties.put("mail.smtp.port", props.getProperty("MAIL_SMTP_PORT"));
        properties.put("mail.smtp.auth", props.getProperty("MAIL_SMTP_AUTH"));
        if ("true".equals(props.getProperty("MAIL_SMTP_AUTH"))) {     //是否需要进行安全验证
            authenticator = new UserAuthentication(props.getProperty("MAIL_USER"), props.getProperty("MAIL_PWD"));
        }
        //根据邮件会话属性和密码验证器构造一个发送邮件的session会话
        sendMailSession = Session.getDefaultInstance(properties, authenticator);
        //根据session会话,获得发送连接
        trans = sendMailSession.getTransport("smtp");
        trans.connect(props.getProperty("MAIL_SMTP_HOST"), props.getProperty("MAIL_USER"), props.getProperty("MAIL_PWD"));
        System.out.println("<<<<===============连接成功===============>>>>");
    }

    /**
     * 发送邮件类
     *
     * @param mail
     * @return
     */
    public boolean sendMail(MailEntity mail) {
        boolean flag = true;
        try {
            this.connect();  //连接邮件服务器操作
            Message mailMessage = null;
            if (!mail.isContainAttach()) {
                mailMessage = getNormalMail(mail);
            } else {
                mailMessage = getArchivesMail(mail);
            }
            trans.send(mailMessage);
            System.out.println("<<<<===============发送成功===============>>>>");
        } catch (Exception e) {
            flag = false;
            System.out.println("<<<<===============发送失败===============>>>>");
            e.printStackTrace();
        } finally {
            try {
                trans.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    /**
     * 生成简单邮件消息
     *
     * @param mail
     * @return
     * @throws Exception
     */
    private Message getNormalMail(MailEntity mail) throws Exception {
        //根据session创建一个邮件消息
        Message mailMessage = new MimeMessage(sendMailSession);
        //设置邮件消息的发送者
        mailMessage.setFrom(new InternetAddress(mail.getFrom()));
        //设置邮件的接收者地址
        mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(mail.getToMails()));
        //设置邮件的主题
        mailMessage.setSubject(mail.getSubject());
        //设置邮件发送的时间
        mailMessage.setSentDate(new Date());
        //设置邮件的内容
        mailMessage.setText(mail.getContent());
        return mailMessage;
    }

    /**
     * 生成带附件的邮件消息
     *
     * @param mail
     * @return
     * @throws Exception
     */
    private Message getArchivesMail(MailEntity mail) throws Exception {
        Message mailMessage = new MimeMessage(sendMailSession);
        //设置邮件消息的发送者
        mailMessage.setFrom(new InternetAddress(mail.getFrom()));
        //设置邮件的接收者地址
        mailMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(mail.getToMails()));
        //设置邮件的主题
        mailMessage.setSubject(mail.getSubject());
        //设置邮件信息生成时间
        mailMessage.setSentDate(new Date());
        Multipart contentPart = new MimeMultipart();
        //设置邮件内容
        mailMessage.setContent(contentPart);
        //1 邮件文本内容
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(mail.getContent(), "text/html; charset=GBK");
        contentPart.addBodyPart(textPart);//将文本部分，添加到邮件内容
        //2 附件
        if (mail.getArchives() != null) {
            for (int i = 0; i < mail.getArchives().length; i++) {
                MimeBodyPart archivePart = new MimeBodyPart();
                //选择出每一个附件文件名
                String filename = mail.getArchives()[i];
                //得到数据源
                FileDataSource fds = new FileDataSource(filename);
                //得到附件本身并至入BodyPart
                archivePart.setDataHandler(new DataHandler(fds));
                //得到文件名同样至入BodyPart
                archivePart.setFileName(fds.getName());
                // 将附件添加到附件集
                contentPart.addBodyPart(archivePart);
            }
        }
        return mailMessage;
    }
}
