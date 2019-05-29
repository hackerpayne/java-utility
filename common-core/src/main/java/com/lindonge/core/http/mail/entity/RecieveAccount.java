package com.lindonge.core.http.mail.entity;

import com.lindonge.core.util.Utils;
import com.lindonge.core.file.FileUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class RecieveAccount implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private String attach_path = FileUtil.getFile(Utils.CurrentDir, "recieveMailAttach").getAbsolutePath();//附件存放目录
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
