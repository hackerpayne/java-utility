package com.lingdonge.spring.mail;

public interface MailService {
    /**
     * 发送纯文本邮件
     *
     * @param to      接收人的邮箱
     * @param subject 邮件主题
     * @param text    邮件文本
     */
    void sendTextEmail(String to, String subject, String text);

    /**
     * 发送网页邮件
     *
     * @param to      接收人的邮箱
     * @param subject 邮件主题
     * @param html    网页
     */
    void sendHtmlEmail(String to, String subject, String html);

    /**
     * 发送附件的邮件
     *
     * @param to      接收人的邮箱
     * @param subject 邮件主题
     * @param text    邮件文本
     * @param files   附件的文件地址 c:xxx.jpg
     */
    void sendAttachmentsMail(String to, String subject, String text, String... files);

    /**
     * 发送带HTML和附件的Email
     *
     * @param to
     * @param subject
     * @param html
     * @param files
     */
    void sendAttachmentsHtmlMail(String to, String subject, String html, String... files);

    /**
     * 发送嵌入静态资源（一般是图片）的邮件
     *
     * @param to
     * @param subject
     * @param html    邮件内容，需要包括一个静态资源的id，比如：<img src=\"cid:rscId01\" >
     * @param rscPath 静态资源路径和文件名
     * @param rscId   静态资源id
     */
    void sendInlineResourceMail(String to, String subject, String html, String rscPath, String rscId);

}
