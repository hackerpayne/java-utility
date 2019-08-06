package com.lingdonge.spring.log;

import org.springframework.stereotype.Component;

/**
 * 日志字段列表
 */
@Component
public class LogFieldConstant {

    /**
     * 链路追踪的ID
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 全局流水号 贯穿一次业务流程的全局流水号
     */
    public static final String TRANS_ID = "transId";

    /**
     * 客户端IP地址 表示服务-客户端系统节点的标识，可以为IP或者DockerID;
     */
    public static final String CLIENT_ADDR = "clientAddr";

    /**
     * 客户端服务名称，需要在RestTemplate或者Feign里面进行服务名称传递
     */
    public static final String CLIENT_SYS_NAME = "clientSysName";

    /**
     * 服务器IP地址 表示服务-服务端系统节点的标识，可以为IP或者DockerID;
     */
    public static final String SERVER_ADDR = "serverAddr";

    /**
     * 服务端名称
     */
    public static final String SERVER_SYS_NAME = "serverSysName";

    /**
     * 日志系统类型 枚举，REQ-接口请求报文，RESP-接口响应报文，BIZ-通用日志;
     */
    public static final String LOG_TYPE = LogTypeEnum.BIZ.getKey();

}
