package com.lingdonge.core.http.mail.entity;

import com.lingdonge.core.bean.base.BaseEntity;
import com.lingdonge.core.file.FileUtil;
import com.lingdonge.core.util.Utils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecieveAccount extends BaseEntity {

    /**
     * 收件服务器IP
     */
    private String host = "pop3.163.com";//服务器ip

    private int port = 110;//端口

    private EnumRecieveType recieveType = EnumRecieveType.POP3;//服务类型

    private String auth = "true";

    /**
     * 默认不保存附件
     */
    private boolean shoudSaveAttach = false;
    /**
     * 附件保存路径
     */
    private String attach_path = FileUtil.file(Utils.CurrentDir, "recieveMailAttach").getAbsolutePath();//附件存放目录
    /**
     * 收取用户名
     */
    private String userName;
    /**
     * 收取密码
     */
    private String password;

    private boolean enableSSL;


}
