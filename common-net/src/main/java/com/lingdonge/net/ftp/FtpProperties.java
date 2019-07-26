package com.lingdonge.net.ftp;

import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Data;

@Data
public class FtpProperties extends BaseEntity {

    private String host;    //ftp服务器ip

    private int port;        //ftp服务器端口

    private String username;//用户名

    private String password;//密码

    private String basePath;//存放文件的基本路径

}
