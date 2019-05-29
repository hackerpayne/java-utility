package com.lingdonge.mongodb;

import com.lingdonge.mongodb.configuration.MongodbProperties;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Mongodb连接创建类
 */
public class MongodbConnUtil {

    /**
     * 连接到MongoDB服务，如果是远程连接可以将localhost改为服务器所在的IP地址
     *
     * @param properties
     * @return
     */
    public static List<ServerAddress> createServerAddress(MongodbProperties properties) {

        // MongoDB地址列表
        List<ServerAddress> serverAddresses = new ArrayList<>();

        if (null != properties.getAddress() && properties.getAddress().size() > 0) {
            for (String address : properties.getAddress()) {
                String[] hostAndPort = address.split(":");
                String host = hostAndPort[0];
                Integer port = Integer.parseInt(hostAndPort[1]);

                serverAddresses.add(new ServerAddress(host, port));
            }
        } else {
            serverAddresses.add(new ServerAddress(properties.getHost(), properties.getPort()));
        }

        return serverAddresses;
    }

    /**
     * createScramSha1Credential（）参数分别为用户名、数据库名称、密码
     *
     * @param properties
     * @return
     */
    public static MongoCredential createCredential(MongodbProperties properties) {

        // 连接认证
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(properties.getUsername(),
                properties.getAuthenticationDatabase() != null ? properties.getAuthenticationDatabase() : properties.getDatabase(),
                properties.getPassword().toCharArray());

//        List<MongoCredential> mongoCredentialList = new ArrayList<MongoCredential>();
//        mongoCredentialList.add(mongoCredential);
//        return mongoCredentialList;
        return mongoCredential;
    }

    /**
     * 创建连接池
     *
     * @param properties
     * @return
     */
    public static MongoClient createMongodbFactoryClient(MongodbProperties properties) {
        // 客户端配置（连接数，副本集群验证）
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.connectionsPerHost(properties.getMaxConnectionsPerHost());
        builder.minConnectionsPerHost(properties.getMinConnectionsPerHost());
        if (properties.getReplicaSet() != null) {
            builder.requiredReplicaSetName(properties.getReplicaSet());
        }

        builder.threadsAllowedToBlockForConnectionMultiplier(properties.getThreadsAllowedToBlockForConnectionMultiplier());
        builder.serverSelectionTimeout(properties.getServerSelectionTimeout());
        builder.maxWaitTime(properties.getMaxWaitTime());
        builder.maxConnectionIdleTime(properties.getMaxConnectionIdleTime());
        builder.maxConnectionLifeTime(properties.getMaxConnectionLifeTime());
        builder.connectTimeout(properties.getConnectTimeout());
        builder.socketTimeout(properties.getSocketTimeout());
//        builder.socketKeepAlive(properties.getSocketKeepAlive());
        builder.sslEnabled(properties.getSslEnabled());
        builder.sslInvalidHostNameAllowed(properties.getSslInvalidHostNameAllowed());
        builder.alwaysUseMBeans(properties.getAlwaysUseMBeans());
        builder.heartbeatFrequency(properties.getHeartbeatFrequency());
        builder.minHeartbeatFrequency(properties.getMinHeartbeatFrequency());
        builder.heartbeatConnectTimeout(properties.getHeartbeatConnectTimeout());
        builder.heartbeatSocketTimeout(properties.getHeartbeatSocketTimeout());
        builder.localThreshold(properties.getLocalThreshold());
        MongoClientOptions mongoClientOptions = builder.build();

        // 创建客户端
        return new MongoClient(createServerAddress(properties), createCredential(properties), mongoClientOptions);
    }

    /**
     * 根据配置创建工厂
     *
     * @param properties
     * @return
     */
    public static MongoDbFactory createMongodbFactory(MongodbProperties properties) {

        // 创建客户端和Factory
        MongoClient mongoClient = createMongodbFactoryClient(properties);

        return new SimpleMongoDbFactory(mongoClient, properties.getDatabase());
    }


}
