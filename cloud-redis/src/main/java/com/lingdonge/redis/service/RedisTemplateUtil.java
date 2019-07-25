package com.lingdonge.redis.service;

import com.lingdonge.redis.util.RedisConnUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 封装RedisTemplate的实现
 * StringRedisTemplate和RedisTemplate的区别：
 * 1、StringRedisTemplate继承了RedisTemplate。
 * 2、RedisTemplate是一个泛型类，而StringRedisTemplate则不是。
 * 3、StringRedisTemplate只能对key=String，value=String的键值对进行操作，RedisTemplate可以对任何类型的key-value键值对操作。
 * 4、各自序列化的方式不同，但最终都是得到了一个字节数组，殊途同归，StringRedisTemplate使用的是StringRedisSerializer类；RedisTemplate使用的是JdkSerializationRedisSerializer类。反序列化，则是一个得到String，一个得到Object
 */
@Slf4j
public class RedisTemplateUtil {

    private RedisTemplate redisTemplate;

    public RedisTemplateUtil() {
//        logger.info("<<<<<<<<<<<<<<< 初始化 RedisService >>>>>>>>>>>>>>>>>>");
    }

    /**
     * 注入Redis
     *
     * @param redisProperties
     */
    public RedisTemplateUtil(RedisProperties redisProperties) {
        this.redisTemplate = RedisConnUtil.getRedisTemplate(redisProperties);
        this.afterPropertySet();//注入和生成实例
    }

    /**
     * 构造函数，直接生成
     *
     * @param redisTemplate
     */
    public RedisTemplateUtil(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.afterPropertySet();//注入和生成实例
    }

    private static volatile RedisTemplateUtil instance;

    /**
     * 单例模式，线程安全
     *
     * @return
     */
    public static RedisTemplateUtil getInstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (RedisTemplateUtil.class) {
                //未初始化，则初始instance变量
                if (instance == null) {
                    instance = new RedisTemplateUtil();
                    instance.afterPropertySet();//初始化配置和Pool池
                }
            }
        }
        return instance;
    }

    /**
     * 构造完之后执行。
     */
    public void afterPropertySet() {
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.afterPropertySet();//注入和生成实例
    }

    /**
     * 查询所有对象
     */
    public List<Object> findAll() {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        Collection<String> strs = redisTemplate.keys("*");
        return operations.multiGet(strs);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * @param key
     * @param <T>
     * @return
     */
    public <T> T getTo(final String key) {

        if (redisTemplate.hasKey(key)) {
            ValueOperations<Serializable, T> operations = redisTemplate.opsForValue();
            return operations.get(key);
        }
        return null;
    }

    /**
     * 获取到指定类型
     *
     * @param key
     * @param defaultVal
     * @param <T>
     * @return
     */
    public <T> T getTo(final String key, T defaultVal) {

        if (redisTemplate.hasKey(key)) {
            ValueOperations<Serializable, T> operations = redisTemplate.opsForValue();
            return operations.get(key);
        } else {
            return defaultVal;
        }
    }

    /**
     * 获取Number值，异常和空值使用0
     *
     * @param key
     * @return
     */
    public Number getNumber(final String key) {
        return getNumber(key, 0);
    }

    /**
     * 从Redis中获取Long型的数据
     * 说明：在Redis存Long或者incr之后的结果时，由于设置了序列化规则，所以取出来的是Object对象，在转换时，会造成数据错误。需要使用此方便获取Long型的数据
     * 具体会抛错：存入Long对象取出Integer对象，ClassCastException: java.lang.Integer cannot be cast to java.lang.Long。
     * 原因：RedisTemplate默认使用的Json序列化工具，会把结果反序列化为Object类型，所以这里便是问题的根源所在，对于数值类型，取出后统一转为Object,导致泛型类型丢失，数值自动转为了Integer类型也就不奇怪了。
     * 影响：Jackson2JsonRedisSerializer 都会影响
     * 参考：https://blog.csdn.net/zhanngle/article/details/51363762
     * https://blog.csdn.net/weixin_33881041/article/details/91472219
     *
     * @param key
     * @return
     */
    public Number getNumber(final String key, Number defaultNumber) {
//        ValueOperations<String, Number> valueOperations = redisTemplate.opsForValue();
//        return valueOperations.get(key);
        return (Number) redisTemplate.execute((RedisCallback<Number>) connection -> {
            RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
            byte[] rowkey = serializer.serialize(key);
            byte[] rowval = connection.get(rowkey);
            try {
                String val = serializer.deserialize(rowval);
                if (StringUtils.isNotEmpty(val)) {
                    val = val.replace(" ", "").replace("\"", "");
                }
                Number value = NumberUtils.createNumber(val);
                return value == null ? defaultNumber : value;
            } catch (Exception e) {
                return defaultNumber;
            }
        });
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 保存String值，带过期时间
     *
     * @param key
     * @param value
     * @param expireSeconds 多少秒后过期，指定过期的秒数，非指定时间过期
     * @return
     */
    public boolean set(String key, Object value, Long expireSeconds) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);

            if (expireSeconds > 0) {
                redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
            }

            result = true;
        } catch (Exception e) {
            log.error("Redis使用set异常：");
        }
        return result;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Incr自增1
     *
     * @param key
     * @return
     */
    public Integer incr(String key) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return operations.increment(key, 1L).intValue();
    }

    /**
     * IncrBy 按指定的长度递增
     *
     * @param key
     * @return
     */
    public Integer incrBy(String key, Integer value) {
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        return operations.increment(key, value).intValue();
    }

    /**
     * @param key
     */
    public void delete(String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 删除一个或多个值
     *
     * @param keys
     */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * @param pattern
     */
    public void removePattern(String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * @param key
     * @return
     */
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key, hashKey, value);
    }

    /**
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    /**
     * @param k
     * @param v
     */
    public void lPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.leftPush(k, v);
    }

    public Object lPop(String k) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.leftPop(k);
    }

    public void rPush(String k, Object v) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k, v);
    }

    public Object rPop(String k) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.rightPop(k);
    }

    /**
     * @param k
     * @param start
     * @param end
     * @return
     */
    public List<Object> lRange(String k, long start, long end) {
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k, start, end);
    }

    /**
     * @param key
     * @param value
     */
    public void sAdd(String key, Object value) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key, value);
    }

    /**
     * 返回所有Members列表
     *
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key) {
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * @param key
     * @param value
     * @param scoure
     */
    public void zAdd(String key, Object value, double scoure) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        zset.add(key, value, scoure);
    }

    /**
     * Redis扫描，默认没有实现，这里手动实现Scan功能
     *
     * @param pattern
     * @param limit
     * @return
     */
    @SuppressWarnings("unchecked")
    public Cursor<String> scan(String pattern, int limit) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).count(limit).build();
        RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();

        // 注意这里使用了 executeWithStickyConnection，因为 SCAN 需要在同一条连接上执行。
        return (Cursor) redisTemplate.executeWithStickyConnection(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                return new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize);
            }
        });
    }

    /**
     * 获取符合条件的key
     *
     * @param pattern 表达式
     * @return
     */
    public List<String> keys(String pattern) {
        List<String> keys = new ArrayList<>();
        Cursor<String> listCursors = scan(pattern, Integer.MAX_VALUE);
        while (listCursors.hasNext()) {
            keys.add(listCursors.next());
        }
        return keys;
    }

    /**
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }

    /**
     * 获取token的有效期---秒
     *
     * @param key
     * @return
     */
    public long getExpireTimeType(String key) {
        long time = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return time;
    }

    /**
     * 获取token的有效期---分
     *
     * @return
     */
    public long getExpireTimeTypeForMin(String key) {
        long time = redisTemplate.getExpire(key, TimeUnit.MINUTES);
        return time;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        if (time > 0) {
            return redisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
        return false;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) expire(key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) expire(key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
