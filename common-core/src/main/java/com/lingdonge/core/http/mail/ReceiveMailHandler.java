package com.lingdonge.core.http.mail;

import com.google.common.collect.Lists;
import com.lingdonge.core.http.mail.entity.MailEntity;
import com.lingdonge.core.http.mail.entity.RecieveAccount;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;
import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.security.Security;
import java.util.List;
import java.util.Properties;

import static com.lingdonge.core.http.mail.entity.EnumRecieveType.POP3;

/**
 * 邮件的收取类
 * http://blog.csdn.net/lanjian056/article/details/52711305
 */
@Slf4j
public class ReceiveMailHandler {

    /**
     * 获取session会话的方法
     *
     * @return
     * @throws Exception
     */
    private Store getSessionMail(RecieveAccount account) throws Exception {
        Properties properties = System.getProperties();

        String SSL_FACTORY = "";
        if (account.isEnableSSL()) {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        }
        switch (account.getRecieveType()) {
            case POP3:
                properties.put("mail.smtp.host", account.getHost());
                properties.put("mail.smtp.auth", account.getAuth());
                properties.put("mail.smtp.port", account.getPort());

                if (account.isEnableSSL()) {
                    properties.put("mail.smtp.starttls.enable", "true");
                    properties.put("mail.smtp.socketFactory.class", SSL_FACTORY);
                    properties.put("mail.smtp.socketFactory.fallback", "false");
                    properties.put("mail.smtp.socketFactory.port", account.getPort());
                }
                properties.put("mail.transport.protocol", "pop3"); // 使用的协议（JavaMail规范要求）
                properties.put("mail.store.protocol", "pop3");
                break;
            case IMAP:
                properties.put("mail.store.protocol", "imap");
                properties.put("mail.imap.host", account.getHost());
                properties.put("mail.imap.fetchsize", "819200");//The default IMAP implementation in JavaMail is very slow to download large attachments. Reason for this is that, by default, it uses a small 16K fetch buffer size.        You can increase this buffer size using the “mail.imap.fetchsize” system property。原来我900k的附件下载需要60多s,加完之后只需要2-3s
                properties.put("mail.imap.port", account.getPort());

                if (account.isEnableSSL()) {
                    // IMAP provider
                    properties.put("mail.imap.socketFactory.class", SSL_FACTORY);
                    properties.put("mail.imap.socketFactory.fallback", "false");
                    properties.put("mail.imap.socketFactory.port", account.getPort());
                }
                break;
            default:
                break;
        }

        Session sessionMail = Session.getDefaultInstance(properties, null);//获取共享的session对象

//        URLName urln = new URLName(profile.getRecieveType().toString().toLowerCase(), profile.getHost(), profile.getPort(), null, profile.getUserName(), profile.getPassword());
//        Store store = sessionMail.getStore(urln);

        // 连接方式二
        Store store = sessionMail.getStore(account.getRecieveType().toString().toLowerCase());
        store.connect(account.getHost(), account.getPort(), account.getUserName(), account.getPassword());
        return store;
    }

    /**
     * 接收邮件
     *
     * @param account   接收账户信息
     * @param mailCount 获取多不封邮件回来
     */
    public List<MailEntity> receiveMail(RecieveAccount account, Integer mailCount) {
        Store store = null;
        Folder folder = null;
        int messageCount = 0;

        List<MailEntity> listMails = Lists.newArrayList();
        try {
            store = getSessionMail(account);
//            store.connect();

            //获得邮箱内的邮件夹Folder对象，以"只读"打开
            folder = store.getFolder("INBOX");//打开收件箱
            folder.open(Folder.READ_ONLY);//设置只读

            //获得邮件夹Folder内的所有邮件个数
            messageCount = folder.getMessageCount();// 获取所有邮件个数

            //获取新邮件处理
            log.info("============>>邮件总数：" + messageCount);

            if (messageCount > 0) {
                Message[] messages = null;
                if (mailCount <= 0) {
                    messages = folder.getMessages();//读取最近的一封邮件
                } else {
                    Integer start = 1;//总数减去要取出的数据
                    if (messageCount > mailCount) {
                        start = messageCount - mailCount;
                    }
                    messages = folder.getMessages(start, messageCount);//读取最近的一封邮件
                }

                listMails = parseMessage(account, messages);// 解析并返回所有邮件内容

//                for (int i = 0; i < messages.length; i++) {
//                    String content = getMailContent((Part) messages[i]);//获取内容
//                    if (isContainAttach((Part) messages[i])) {
//                        saveAttachMent((Part) messages[i], profile.getAttach_path());
//                    }
//                    System.out.println("=====================>>开始显示邮件内容<<=====================");
//                    System.out.println("发送人: " + getFrom(messages[i]));
//                    System.out.println("主题: " + getSubject(messages[i]));
//                    System.out.println("内容: " + content);
//                    System.out.println("发送时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((MimeMessage) messages[i]).getSentDate()));
//                    System.out.println("是否有附件: " + (isContainAttach((Part) messages[i]) ? "有附件" : "无附件"));
//                    System.out.println("=====================>>结束显示邮件内容<<=====================");
//                    ((POP3Message) messages[i]).invalidate(true);
//                }
            }
            return listMails;
        } catch (Exception e) {
            log.error("收件时发生异常", e);
            return listMails;
        } finally {
            if (folder != null && folder.isOpen()) {
                try {
                    folder.close(true);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            if (store.isConnected()) {
                try {
                    store.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 解析邮件
     *
     * @param account  要读取的账号的信息
     * @param messages 要解析的邮件列表
     */
    public List<MailEntity> parseMessage(RecieveAccount account, Message... messages) throws MessagingException, IOException {
        if (messages == null || messages.length < 1) {
            throw new MessagingException("未找到要解析的邮件!");
        }

        List<MailEntity> listMails = Lists.newArrayList();

        // 解析所有邮件
        MailEntity emaininfo;
        for (int i = 0, count = messages.length; i < count; i++) {
            MimeMessage msg = (MimeMessage) messages[i];
            msg.setFlag(Flags.Flag.SEEN, true);

            //存储邮件信息
            emaininfo = new MailEntity();
            emaininfo.setMesId(msg.getMessageID()); //ID
            emaininfo.setFrom(getFrom(msg));//张三<zhangsan@163.com>
            emaininfo.setSubject(msg.getSubject());//转码后的标题
//            emaininfo.setToMails(getReceiveAddress(msg, null));//收件人
            emaininfo.setRecieveDate(msg.getSentDate());//收件日期
            emaininfo.setContent(getMailContent(msg));//内容
            emaininfo.setSendDate(msg.getSentDate());

            // 存在附件且设置了需要保存时，才保存附件信息
            if (isContainAttach(msg) && account.isShoudSaveAttach()) {
                emaininfo.setContainAttach(true);
                saveAttachMent(msg, emaininfo.getSaveAttatchPath());
            }
            listMails.add(emaininfo);

        }
        return listMails;
    }

    /**
     * 只收取未读邮件
     *
     * @param account
     * @throws Exception
     */
    public void getUnreadMail(RecieveAccount account) throws Exception {
        Store store = getSessionMail(account);

        log.info("login email:{} server:", account.getUserName().toString(), account.getHost().toString());

        Folder folder = store.getFolder("INBOX");

        folder.open(Folder.READ_ONLY);

        try {

            if (folder instanceof POP3Folder) {
                POP3Folder inbox = (POP3Folder) folder;
                Message[] messages = inbox.getMessages();

                for (int i = 0; i < messages.length; i++) {
                    MimeMessage mimeMessage = (MimeMessage) messages[i];

                    // 删除邮件
                    // message.setFlag(Flags.Flag.DELETED, true);
                    // 标记为已读
                    // message.setFlag(Flags.Flag.SEEN, true);

                    String uid = inbox.getUID(mimeMessage);//千万不要用mimeMessage.getMessageID();这个方法，这个方法会去下载邮件头，是一个很耗时的过程
//                    if (!emailUidDB.get(userSession.getUserId(), uid)) {
//                        receive(mimeMessage, uid);
//                    }
                }

            } else if (folder instanceof IMAPFolder) {

                IMAPFolder inbox = (IMAPFolder) folder;
                Message[] messages = inbox.getMessages();
                for (int i = 0; i < messages.length; i++) {

                    MimeMessage mimeMessage = (MimeMessage) messages[i];

                    String uid = Long.toString(inbox.getUID(mimeMessage));

//                    if (!emailUidDB.get(userSession.getUserId(), uid)) {
//                        receive(mimeMessage, uid);
//                    }
                }

            } else {

                log.error("no have this folder {}", folder);

            }

//            emailAccountBean.setTime(System.currentTimeMillis());//收件时间

        } finally {
            folder.close(false);
            store.close();
        }
    }

    /**
     * 获得发件人的地址
     *
     * @param message：Message
     * @return 发件人的地址
     */
    private String getFrom(Message message) {
        InternetAddress[] address = new InternetAddress[0];
        try {
            address = (InternetAddress[]) ((MimeMessage) message).getFrom();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        String from = address[0].getAddress();
        if (from == null) {
            from = "";
        }
        return from;
    }

    /**
     * 获得邮件主题
     *
     * @param message：Message
     * @return 邮件主题
     */
    private String getSubject(Message message) {
        String subject = "";
        try {
            if (((MimeMessage) message).getSubject() != null) {
                subject = MimeUtility.decodeText(((MimeMessage) message).getSubject());// 将邮件主题解码
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }

        return subject;
    }

    /**
     * 获取邮件内容
     *
     * @param part：Part
     */
    private String getMailContent(Part part) {
        StringBuffer bodytext = new StringBuffer();//存放邮件内容
        //判断邮件类型,不同类型操作不同
        try {
            if (part.isMimeType("text/plain")) {
                bodytext.append((String) part.getContent());
            } else if (part.isMimeType("text/html")) {
                bodytext.append((String) part.getContent());
            } else if (part.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) part.getContent();
                int counts = multipart.getCount();
                for (int i = 0; i < counts; i++) {
                    //                getMailContent(multipart.getBodyPart(i));
                    bodytext.append(getMailContent(multipart.getBodyPart(i)));//参才评论进行更新修改
                }
            } else if (part.isMimeType("message/rfc822")) {
                getMailContent((Part) part.getContent());
            } else {
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bodytext.toString();
    }

    /**
     * 判断此邮件是否包含附件
     *
     * @param part：Part
     * @return 是否包含附件
     */
    private boolean isContainAttach(Part part) {
        boolean attachflag = false;
        try {
            if (part.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart mpart = mp.getBodyPart(i);
                    String disposition = mpart.getDisposition();
                    if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE))))
                        attachflag = true;
                    else if (mpart.isMimeType("multipart/*")) {
                        attachflag = isContainAttach((Part) mpart);
                    } else {
                        String contype = mpart.getContentType();
                        if (contype.toLowerCase().indexOf("application") != -1)
                            attachflag = true;
                        if (contype.toLowerCase().indexOf("name") != -1)
                            attachflag = true;
                    }
                }
            } else if (part.isMimeType("message/rfc822")) {
                attachflag = isContainAttach((Part) part.getContent());
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return attachflag;
    }

    /**
     * 保存附件
     *
     * @param part：Part
     * @param filePath：邮件附件存放路径
     */
    private void saveAttachMent(Part part, String filePath) {
        String fileName = "";
        //保存附件到服务器本地
        try {
            if (part.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) part.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart mpart = mp.getBodyPart(i);
                    String disposition = mpart.getDisposition();
                    if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
                        fileName = mpart.getFileName();
                        if (fileName != null) {
                            fileName = MimeUtility.decodeText(fileName);
                            saveFile(fileName, mpart.getInputStream(), filePath);
                        }
                    } else if (mpart.isMimeType("multipart/*")) {
                        saveAttachMent(mpart, filePath);
                    } else {
                        fileName = mpart.getFileName();
                        if (fileName != null) {
                            fileName = MimeUtility.decodeText(fileName);
                            saveFile(fileName, mpart.getInputStream(), filePath);
                        }
                    }
                }
            } else if (part.isMimeType("message/rfc822")) {
                saveAttachMent((Part) part.getContent(), filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 保存附件到指定目录里
     *
     * @param fileName：附件名称
     * @param in：文件输入流
     * @param filePath：邮件附件存放基路径
     */
    private void saveFile(String fileName, InputStream in, String filePath) throws Exception {
        File storefile = new File(filePath);
        if (!storefile.exists()) {
            storefile.mkdirs();
        }
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filePath + "\\" + fileName));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
    }
}