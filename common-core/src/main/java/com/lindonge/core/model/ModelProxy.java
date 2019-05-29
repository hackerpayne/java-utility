package com.lindonge.core.model;


import lombok.Data;

import java.io.Serializable;

/**
 * 代理IP实体类
 */
@Data
public class ModelProxy implements Serializable {

    private static final long serialVersionUID = -3699072211264713025L;

    private String host;

    private int port;

    private String username;

    private String password;

    public ModelProxy() {

    }

    /**
     * @param host
     * @param port
     */
    public ModelProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * @param host
     * @param port
     * @param username
     * @param password
     */
    public ModelProxy(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ModelProxy proxy = (ModelProxy) o;
            if (this.port != proxy.port) {
                return false;
            } else {
                label44:
                {
                    if (this.host != null) {
                        if (this.host.equals(proxy.host)) {
                            break label44;
                        }
                    } else if (proxy.host == null) {
                        break label44;
                    }

                    return false;
                }

                if (this.username != null) {
                    if (!this.username.equals(proxy.username)) {
                        return false;
                    }
                } else if (proxy.username != null) {
                    return false;
                }

                return this.password != null ? this.password.equals(proxy.password) : proxy.password == null;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = this.host != null ? this.host.hashCode() : 0;
        result = 31 * result + this.port;
        result = 31 * result + (this.username != null ? this.username.hashCode() : 0);
        result = 31 * result + (this.password != null ? this.password.hashCode() : 0);
        return result;
    }

    /**
     * 获取代理的字符串表达形式
     *
     * @return
     */
    public String getProxyStr() {
        return this.host.trim() + ":" + this.port;
    }

}