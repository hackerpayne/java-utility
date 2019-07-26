package com.lingdonge.net.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * SSH连接工具
 */
public class SshConnection {

    private Session sesion; //represents each ssh session

    private SshProperties sshProperties;

    public SshConnection(SshProperties sshProperties) {
        this.sshProperties = sshProperties;
    }

    /**
     * 初始化Session
     *
     * @throws JSchException
     */
    public Session initSession() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(this.sshProperties.getKnownHosts());

        if (StringUtils.isNotEmpty(sshProperties.getPrivateKeyFile())) {
            jsch.addIdentity(this.sshProperties.getPrivateKeyFile());
        }

        Session sesion = jsch.getSession(sshProperties.getUser(), sshProperties.getHost(), sshProperties.getPort());

        if (StringUtils.isNotEmpty(sshProperties.getPassword())) {
            sesion.setPassword(sshProperties.getPassword());
        }

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        sesion.setConfig(config);

        sesion.connect(); //ssh connection established!

        return sesion;
    }

    /**
     * 创建一个MySQL的中转连接
     * 连接本机port可以直接中转到远程的mysql上
     *
     * @param mysqlHost MySQL的主机
     * @param localPort 本机端口
     * @param mysqlPort 远程MySQL的端口
     * @throws JSchException
     */
    public void createMySqlForward(String mysqlHost, Integer localPort, Integer mysqlPort) throws JSchException {

        if (null == sesion) {
            sesion = initSession();
        }

        //by security policy, you must connect through a fowarded port
        sesion.setPortForwardingL(localPort, mysqlHost, mysqlPort);
    }

    /**
     * 关闭Session
     */
    public void close() {
        sesion.disconnect();
    }

}