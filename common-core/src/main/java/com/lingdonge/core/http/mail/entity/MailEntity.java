package com.lingdonge.core.http.mail.entity;

import com.lingdonge.core.bean.base.BaseEntity;
import com.lingdonge.core.file.json.FastJsonUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 发送邮件的Mail实体
 */
@Getter
@Setter
public class MailEntity extends BaseEntity {

    /**
     * 邮件id
     */
    private String mesId;
    /**
     * 邮件Uid和MessageID有区别
     */
    private String mesUid;
    private String toMails; //收件人
    private String copyMails; //抄送人
    private String from;      //发送人
    /**
     * 内容
     */
    private String content;
    /**
     * 上传附件路径
     */
    private String[] archives;
    /**
     * 主题
     */
    private String subject;
    /**
     * 是否带有附件
     */
    private boolean containAttach;
    /**
     * 附件保存路径
     */
    private String saveAttatchPath;

    @Override
    public String toString() {
        return FastJsonUtil.toJsonString(this);
    }

    /**
     * 发送时间
     */
    private Date sendDate;
    /**
     * 收件日期
     */
    private Date recieveDate;

}