

新版本Driver：
com.mysql.jdbc.Driver
com.mysql.cj.jdbc.Driver

### Druid数据源：
https://github.com/alibaba/druid
https://github.com/alibaba/druid/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98

SpringBoot自动配置：
https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter

1、添加依赖
<dependency>
   <groupId>com.alibaba</groupId>
   <artifactId>druid-spring-boot-starter</artifactId>
   <version>1.1.10</version>
</dependency>

#### 多数据源配置

1、添加配置
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

# Druid 数据源配置，继承spring.datasource.* 配置，相同则覆盖
...
spring.datasource.druid.initial-size=5
spring.datasource.druid.max-active=5
...

# Druid 数据源 1 配置，继承spring.datasource.druid.* 配置，相同则覆盖
...
spring.datasource.druid.one.max-active=10
spring.datasource.druid.one.max-wait=10000
...

# Druid 数据源 2 配置，继承spring.datasource.druid.* 配置，相同则覆盖
...
spring.datasource.druid.two.max-active=20
spring.datasource.druid.two.max-wait=20000
...
强烈注意：Spring Boot 2.X 版本不再支持配置继承，多数据源的话每个数据源的所有配置都需要单独配置，否则配置不会生效

2、创建数据源  
@Primary
@Bean
@ConfigurationProperties("spring.datasource.druid.one")
public DataSource dataSourceOne(){
    return DruidDataSourceBuilder.create().build();
}
@Bean
@ConfigurationProperties("spring.datasource.druid.two")
public DataSource dataSourceTwo(){
    return DruidDataSourceBuilder.create().build();
}


### Mybatis-Plus配置
https://gitee.com/baomidou/mybatisplus-spring-boot/blob/2.x/src/main/resources/application.yml

