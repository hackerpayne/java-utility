package com.lindonge.core.http.ftp;

public class FtpProperties {

    private String host;    //ftp服务器ip

    private int port;        //ftp服务器端口

    private String username;//用户名

    private String password;//密码

    private String basePath;//存放文件的基本路径

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public String toString() {
        return "FtpUtil [host=" + host + ", port=" + port + ", username=" + username + ", password=" + password
                + ", basePath=" + basePath + "]";
    }


}
