package com.lingdonge.mongodb.configuration;

import com.lingdonge.mongodb.util.MongodbConnUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;

public class MongoDBAutoConfigurationTest {

    private MongodbProperties mongodbProperties;

    /**
     * 需要验证用户名、密码的连接方式
     *
     * @return mongoDatabase
     */
    public MongoDatabase getConnection() {
        try {

            //通过连接认证获取MongoDB连接
            MongoClient mongoClient = new MongoClient(MongodbConnUtil.createServerAddress(mongodbProperties), MongodbConnUtil.createCredential(mongodbProperties), MongoClientOptions.builder().build());

            //连接数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase("databaseName");
            System.out.println("MongoDB连接成功");
            return mongoDatabase;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * 不需要验证用户名、密码的连接方式
     *
     * @return mongoDatabase
     */
    public MongoDatabase getConnectionBasis() {
        try {
            //连接到MongoDB服务
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            MongoDatabase mongoDatabase = mongoClient.getDatabase("users");
            System.out.println("MongoDB连接成功");
            return mongoDatabase;
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + "：" + e.getMessage());
        }
        return null;
    }

}