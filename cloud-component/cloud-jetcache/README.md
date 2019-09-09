
### 基础配置
application.yml文件，把他放到资源目录中
```
jetcache:
  statIntervalMinutes: 15
  areaInCacheName: false
  local:
    default:
      type: linkedhashmap
      keyConvertor: fastjson
  remote:
    default:
      type: redis
      keyConvertor: fastjson
      valueEncoder: java
      valueDecoder: java
      poolConfig:
        minIdle: 5
        maxIdle: 20
        maxTotal: 50
      host: 127.0.0.1
      port: 6379
```

或者
```
###jetcache 
jetcache.statIntervalMinutes=15
jetcache.areaInCacheName=false
jetcache.local.default.type=linkedhashmap
jetcache.local.default.keyConvertor=fastjson
jetcache.remote.default.type=redis
jetcache.remote.default.keyConvertor=fastjson
jetcache.remote.default.valueEncoder=java
jetcache.remote.default.valueDecoder=java
jetcache.remote.default.poolConfig.minIdle=5
jetcache.remote.default.poolConfig.maxIdle=20
jetcache.remote.default.poolConfig.maxTotal=50
jetcache.remote.default.host=127.0.0.1
jetcache.remote.default.port=6379
```

### 使用方法
EnableMethodCache，EnableCreateCacheAnnotation这两个注解分别激活Cached和CreateCache注解，其他和标准的Spring Boot程序是一样的。

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMethodCache(basePackages = "com.company.mypackage")
@EnableCreateCacheAnnotation
public class MySpringBootApp {
    public static void main(String[] args) {
        SpringApplication.run(MySpringBootApp.class);
    }
}


### 代码中使用缓存 https://github.com/alibaba/jetcache/wiki/GettingStarted_CN
1、创建缓存实例
通过@CreateCache注解创建一个缓存实例：

@CreateCache
private Cache<Long,ArticleType> cache;

用起来就像map一样：
//ready for entity.
ArticleType articleType = new ArticleType();
articleType.setId(100L);
articleType.setName("小说");
//set to cache.
cache.put(articleType.getId(), articleType);

//get from cache
articleType = cache.get(articleType.getId());

//remove from cache
cache.remove(articleType.getId());

那么这个生成的key的name是：

c.k.j.d.c.TestCacheInstanceController.cache100

那么如何自定义key的名称呢？这个下节在讲jetcache小技巧的时候统一进行讲解。

2、创建方法缓存

使用@Cached方法可以为一个方法添加上缓存。JetCache通过Spring AOP生成代理，来支持缓存功能。注解可以加在接口方法上也可以加在类方法上，但需要保证是个Spring bean：
@Cached
public ArticleType getById(long id) {
    ArticleType articleType = new ArticleType();
    articleType.setId(id);
    articleType.setName("视频-"+id);
    return articleType;
}

这个默认生成的redis的name是：
c.k.j.d.s.ArticleTypeService.getById(J)[102]

3、指定CacheKey
@Cached(name="ArticleType.getById")
public ArticleType getById(long id){}

缓存的名称name，不是必须的，如果没有指定，会使用类名+方法名。name会被用于远程缓存的key前缀。另外在统计中，一个简短有意义的名字会提高可读性。

3.2、缓存刷新
对应的代码如下：
public interface Service {   
      @Cached(cacheType = CacheType.LOCAL)   
      @CacheRefresh(refresh = 60)   
      int printSay(String message);
}

@CacheRefresh上面的配置是1分钟刷新一次
    
4、指定失效时间
@Cached(name="ArticleType.getById",expire=3600)
public ArticleType getById(long id) {}

该Cache实例的默认超时时间定义，注解上没有定义的时候会使用全局配置，如果此时全局配置也没有定义，则取无穷大。缓存失效时间单位是秒，3600秒=1小时。

当然失效时间的单位，你可以通过属性timeUnit（TimeUnit.SECONDS ）重新定义的。

4.1、缓存失效
对应的代码如下：
@CacheInvalidate(name = "c1", key = "args[0]")
void delete(String id);

表示从缓存名称为c1，将对应key为id值的记录从缓存c1中删除。

5、缓存更新
对应的代码如下：

@CacheUpdate(name = "c1", key = "#id", value = "args[1]")
void update(String id, int value);

刷新缓存对应的缓存名称为c1，缓存中对应的key为id的值，更新key的值为value的值。

6、缓存开启

对应的代码如下：

@Cached(enabled = false)
public int countWithDisabledCache(){   
  return count++;
}
@EnableCache
public int enableCacheWithAnnoOnClass(){   
  return countWithDisabledCache();
}

从上面代码中可以看出方法countWithDisabledCache对应的方法定义了缓存功能，但是这个功能被关闭了，而方法enableCacheWithAnnoOnClass方法上开启了缓存的功能，则方法countWithDisabledCache虽然本身的缓存被关闭了，但是调用方法开启了，则方法countWithDisabledCache对应的缓存功能也被开启了。














