package com.lingdonge.net.mail;

import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * 基于Springboot mail的邮件发送服务
 */
@Slf4j
public class MailServiceImpl implements MailService {

    public MailServiceImpl() {
    }

    /**
     * 构造函数
     *
     * @param javaMailSender
     * @param mailProperties
     */
    public MailServiceImpl(JavaMailSender javaMailSender, MailProperties mailProperties) {
        this.javaMailSender = javaMailSender;
        this.mailProperties = mailProperties;
    }

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    @Autowired(required = false)
    private MailProperties mailProperties;

    /**
     * 发送纯文本邮件
     *
     * @param to      接收人的邮箱
     * @param subject 邮件主题
     * @param text    邮件文本
     */
    @Override
    public void sendTextEmail(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        simpleMailMessage.setFrom(mailProperties.getUsername());
        simpleMailMessage.setTo(getToArray(to));

        javaMailSender.send(simpleMailMessage);
    }

    /**
     * 发送网页邮件
     *
     * @param to      接收人的邮箱
     * @param subject 邮件主题
     * @param html    网页
     */
    @Override
    public void sendHtmlEmail(String to, String subject, String html) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(getToArray(to));
            helper.setSubject(subject);
            // "<html><body><img src='http://baidu.com/2/1.jpg' ></body></html>"
            helper.setText(html, true);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }


        javaMailSender.send(mimeMessage);
    }

    /**
     * 发送附件的邮件
     *
     * @param to      接收人的邮箱
     * @param subject 邮件主题
     * @param text    邮件文本
     * @param files   附件的文件地址 c:xxx.jpg
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String text, String... files) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(getToArray(to));
            helper.setSubject(subject);
            helper.setText(text);

            if (files != null) {
                for (String file : files) {
                    FileSystemResource fileSystemResource = new FileSystemResource(new File(file));
                    String fileName = file.substring(file.lastIndexOf(File.separator));
                    helper.addAttachment(fileName, fileSystemResource);
                }
            }
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送带HTML和附件的Email
     *
     * @param to
     * @param subject
     * @param html
     * @param files
     */
    @Override
    public void sendAttachmentsHtmlMail(String to, String subject, String html, String... files) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(getToArray(to));
            helper.setSubject(subject);
            helper.setText(html, true);

            if (files != null) {
                for (String file : files) {
                    FileSystemResource fileSystemResource = new FileSystemResource(new File(file));
                    String fileName = file.substring(file.lastIndexOf(File.separator));
                    helper.addAttachment(fileName, fileSystemResource);
                }
            }
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 发送嵌入静态资源（一般是图片）的邮件
     *
     * @param to
     * @param subject
     * @param html    邮件内容，需要包括一个静态资源的id，比如：<img src=\"cid:rscId01\" >
     * @param rscPath 静态资源路径和文件名
     * @param rscId   静态资源id
     */
    @Override
    public void sendInlineResourceMail(String to, String subject, String html, String rscPath, String rscId) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(getToArray(to));
            helper.setSubject(subject);
            helper.setText(html, true);

            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);

            javaMailSender.send(message);

            log.info("嵌入静态资源的邮件已经发送。");
        } catch (MessagingException e) {
            log.error("发送嵌入静态资源的邮件时发生异常！", e);
        }
    }

    /**
     * 拆分收件人列表
     *
     * @param mailTo
     * @return
     */
    private String[] getToArray(String mailTo) {
        return Splitter.onPattern("[\r|\n|\\||,| |\t]").omitEmptyStrings().trimResults().splitToList(mailTo).toArray(new String[0]);
    }

//    public static void main(String[] args) {
//
//        System.out.println(Joiner.on("---").join(Splitter.onPattern("[\r|\n|\\||,| |\t]").omitEmptyStrings().trimResults().splitToList("hahah,hahh2|,hahahd23").toArray(new String[0])));
//    }

//    @Autowired
//    private FreeMarkerConfigurer freeMarkerConfigurer;
//
//    /**
//     * 发送FreeMaker模板邮件
//     *
//     * @param to           收件人
//     * @param subject      主题
//     * @param templateFile 模板文件
//     * @param teplateModel 模板变量列表
//     */
//    @Test
//    public void sendTemplateMail(String to, String subject, String templateFile, Map<String, Object> teplateModel) {
//        MimeMessage message = null;
//        try {
//            message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setFrom(from);
//            helper.setTo(to);
//            helper.setSubject(subject);
//
////            Map<String, Object> bean = new HashedMap();
////            bean.put("username", "zggdczfr");
//
//            //修改 application.properties 文件中的读取路径
////            FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
////            configurer.setTemplateLoaderPath("classpath:template");
//
//            //读取 html 模板
//            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateFile);
//            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, teplateModel);
//            helper.setText(html, true);
//
//            javaMailSender.send(message);
//
//        } catch (Exception e) {
//            log.error("发送FreeMaker模板邮件时发生异常！", e);
//        }
//
//    }

//    @Autowired
//    VelocityEngine velocityEngine;
//
//    /**
//     * 发送模板邮件，新版已经不再支持Velocity模板
//     * 例：你好， ${username}, 这是一封模板邮件!
//     *
//     * @param to           收件人
//     * @param subject      主题
//     * @param templateFile 邮件模板的文件名，需放到static下面，取名为template.vm然后把文件名传进来
//     * @param teplateModel 模板内容
//     */
//    public void sendVelocityTemplateMail(String to, String subject, String templateFile, Map<String, Object> teplateModel) {
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//            helper.setFrom(from);
//            helper.setTo(to);
//            helper.setSubject(subject);
//
//            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, templateFile, "UTF-8", teplateModel);
//            helper.setText(text, true);
//            javaMailSender.send(mimeMessage);
//
//        } catch (MessagingException e) {
//            e.printStackTrace();
//        }
//    }

}
