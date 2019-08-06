package com.lingdonge.mongodb.configuration;

import com.lingdonge.mongodb.util.MongodbConnUtil;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableConfigurationProperties(MongodbProperties.class) // 开启指定类的配置
@Configuration
@EnableMongoRepositories //启用MongoDB的Repository功能
public class MongoDBAutoConfiguration {

    @Autowired
    private MongodbProperties mongodbProperties;

    /**
     * MongoClientFactoryBean 工厂bean 会负责创建Mongo实例。
     *
     * @return
     */
    @Bean(name = "mongo")
    public MongoClientFactoryBean mongoClientFactoryBean() {
        MongoClientOptions.Builder builder = MongoClientOptions.builder();
        MongoClientOptions build = builder.build();
        MongoCredential credential = MongoCredential.createCredential(mongodbProperties.getUsername(), mongodbProperties.getDatabase(), mongodbProperties.getPassword().toCharArray());
        MongoClientFactoryBean mongoClientFactoryBean = new MongoClientFactoryBean();
        mongoClientFactoryBean.setHost(mongodbProperties.getHost());
        mongoClientFactoryBean.setPort(mongodbProperties.getPort());
        mongoClientFactoryBean.setCredentials(new MongoCredential[]{credential});
        mongoClientFactoryBean.setMongoClientOptions(build);
        return mongoClientFactoryBean;
    }

    /**
     * 覆盖默认的MongoDbFacotry
     * @param properties
     * @return
     */
    @Bean
    @Autowired
    public MongoDbFactory mongoDbFactory(MongodbProperties properties) {
        return MongodbConnUtil.createMongodbFactory(properties);
    }

    /**
     * 创建MongoDB Template模板
     *
     * @return
     */
    @Bean(name = "mongoTemplate")
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(MongodbConnUtil.createMongodbFactory(mongodbProperties));
    }

}
