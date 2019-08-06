package com.lingdonge.net.ssh;

import lombok.Data;

/**
 * SSH连接配置
 */
@Data
public class SshProperties {

    /**
     * KnownHosts文件，一般在
     */
    private String knownHosts = "~/.ssh/known_hosts";

    /**
     * 私钥文件
     */
    private String privateKeyFile = "~/.ssh/id_rsa";

    /**
     * 用户名
     */
    private String user = "root";

    /**
     * 密码
     */
    private String password;

    /**
     * 远程主机地址
     */
    private String host = "localhost";

    /**
     * 远程SSH端口
     */
    private Integer port = 22;

}