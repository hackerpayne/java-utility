package com.lingdonge.redis.configuration;

import com.lingdonge.redis.consumer.ConsumerRedisListener;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;

/**
 * Redis实现消息订阅
 * 使用时：stringRedisTemplate.convertAndSend("string-topic","hello world");
 */
//@Configuration
@AutoConfigureAfter(RedisBasicAutoConfiguration.class) //在这个配置之后进行加载
public class RedisTopicAutoConfiguration {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * 定义监听器
     * @return
     */
    @Bean
    public ConsumerRedisListener consumerRedis() {
        return new ConsumerRedisListener();
    }

    /**
     * 定义队列名称
     * @return
     */
    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("string-topic");
    }

    /**
     * 指定消息放到监控器消费
     * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
     * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        //可以添加多个 messageListener
        container.addMessageListener(consumerRedis(), new PatternTopic("index"));
        container.addMessageListener(consumerRedis(), topic());
        return container;

    }
}
