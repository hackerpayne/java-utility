package com.lingdonge.redis;

import com.lingdonge.redis.configuration.RedisBasicAutoConfiguration;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 自动使用配置文件test.yml加载配置并启动Spring的容器
 * 使用时，必须加上：
 *
 * @EnableAutoConfiguration // 自动加载配置文件
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 使用随机端口
//@SpringBootTest(properties = "spring.jmx.enabled=true") // 指定配置文件的配置
@SpringBootTest(classes = {RedisBasicAutoConfiguration.class})
//@TestPropertySource(locations = "classpath:test.yml")
@TestPropertySource(properties = {"spring.config.location=classpath:test.yml"}) // 使用指定的yml配置启动Test
//@ContextConfiguration(locations = {"classpath:test.yml"}, initializers = {ConfigFileApplicationContextInitializer.class})
public abstract class SpringBaseTest {

}