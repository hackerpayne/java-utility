package com.lingdonge.redis.service;

import com.alibaba.fastjson.JSON;
import com.lindonge.core.convert.ConvertUtil;
import com.lingdonge.redis.RedisConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * 基于官方配置创建的Jedis原生支持库
 */
//@Component
@Slf4j
public class RedisPoolUtil {

    private static RedisProperties properties;

    public RedisPoolUtil() {
    }

    public RedisPoolUtil(RedisProperties pro) {
        RedisPoolUtil.properties = pro;
        this.afterPropertySet();//注入和生成实例
        log.info("正在加载Redis配置文件：" + properties);
    }

    private volatile JedisPool pool;

    private static volatile RedisPoolUtil instance;

    /**
     * 单例模式，线程安全
     *
     * @return
     */
    public static RedisPoolUtil getInstance() {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (RedisPoolUtil.class) {
                //未初始化，则初始instance变量
                if (instance == null) {
                    instance = new RedisPoolUtil();
                    instance.afterPropertySet();//初始化配置和Pool池
                }
            }
        }
        return instance;
    }

    /**
     * 实例化完成之后执行
     */
    public void afterPropertySet() {

        // Spring 1.x
//        pool = new JedisPool(config, properties.getHost(), properties.getPort(), properties.getTimeout(), properties.getPassword(), properties.getDatabase());

        // Spring 2.x
        pool = new JedisPool(RedisConfigUtil.getJedisPoolGenericConfig(properties), properties.getHost(), properties.getPort(), ((int) (properties.getTimeout().toMillis())), properties.getPassword(), properties.getDatabase());
    }

    /**
     * 获取一个JedisPool对象
     *
     * @return
     * @throws Exception
     */
    public synchronized static JedisPool getJedisPool() {
        RedisPoolUtil redis = getInstance();
        return redis.pool;
    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public synchronized Jedis getJedis() {
        try {
            if (pool != null) {
                Jedis resource = pool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 关闭当前连接实例，将连接返回连接池
     *
     * @param jedis redis连接实例
     */
    public static void close(Jedis jedis) {
        try {
            if (null != jedis) {
                jedis.close();
            }
        } catch (Exception e) {
            log.error("close jedis failed!", e);
        }
    }

    /**
     * 关闭整个pool
     */
    public void closePool() {
        try {
            if (pool != null) {
                pool.close();
            }
        } catch (Exception e) {
            log.error("close jedis pool failed!", e);
        }
    }

    /**
     * 返回满足pattern表达式的所有key
     *
     * @param keyPattern 键值
     * @return
     */
    public Set<String> keys(String keyPattern) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.keys(keyPattern);
        } catch (Exception e) {
            log.error("redis keys failed!", e);
            return new HashSet<String>();
        } finally {
            close(jedis);
        }
    }


    /**
     * @param key     键值
     * @param seconds
     * @return
     */
    public Long expire(String key, int seconds) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.expire(key.getBytes(), seconds);
        } catch (Exception e) {
            log.error("redis expire failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }

    /**
     * 通过key判断值得类型
     *
     * @param key 键值
     * @return
     */
    public String type(String key) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.type(key);
        } catch (Exception e) {
            log.error("redis type failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key key
     * @return true:存在，false:不存在
     */
    public boolean exists(String key) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("redis exists failed!", e);
            return false;
        } finally {
            close(jedis);
        }
    }


    /**
     * 删除redis中key对应数据,可以指定多个key
     *
     * @param key 键值
     * @return 成功\失败
     */
    public boolean delete(String... key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.del(key);
        } catch (Exception e) {
            log.error("redis delete failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }

    /**
     * 删除redis中key对应数据
     *
     * @param key 键值
     * @return 删除的条数
     */
    public long deleteRegEx(String key) {
        Jedis jedis = null;
        long count = 0;

        try {
            jedis = getJedis();
            Set<String> keys = jedis.keys(key);

            if (keys == null || keys.isEmpty()) {
                return 0;
            }

            for (String sigleKey : keys) {
                jedis.del(sigleKey);
                count++;
            }
            return count;

        } catch (Exception e) {
            log.error("redis deleteRegEx failed", e);
            return -1;
        } finally {
            close(jedis);
        }
    }


    /**
     * 获取key的剩余过期时间，单位：秒
     *
     * @param key 键值
     * @return
     */
    public Long getExpireSeconds(String key) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.ttl(key.getBytes());

        } catch (Exception e) {
            log.error("redis getExpireSeconds failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 获取数据
     *
     * @param key 键值
     * @return
     */
    public String get(String key) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.get(key);
        } catch (Exception e) {
            log.error("redis get failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 获取数据
     *
     * @param key 键值
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            byte[] value = jedis.get(key.getBytes());

            if (value != null) {
                return ConvertUtil.unserialize(value, clazz);
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("redis get failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }


    /**
     * 设置key的值,并返回一个旧值
     *
     * @param key   键值
     * @param value
     * @return 旧值 如果key不存在 则返回null
     */
    public String getset(String key, String value) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.getSet(key, value);
        } catch (Exception e) {
            log.error("redis getset failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过下标 和key 获取指定下标位置的 value
     *
     * @param key         键值
     * @param startOffset 开始位置 从0 开始 负数表示从右边开始截取
     * @param endOffset
     * @return 如果没有返回null
     */
    public String getrange(String key, int startOffset, int endOffset) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e) {
            log.error("redis getrange failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 设置字符串，不设过期时间
     *
     * @param key   键值
     * @param value
     * @return
     */
    public String setString(String key, String value) {
        return setString(key, value, -1);
    }

    /**
     * 设置字符串，可设过期时间
     *
     * @param key     键值
     * @param value
     * @param seconds
     * @return
     */
    public String setString(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            value = StringUtils.isEmpty(value) ? "" : value;

            jedis = getJedis();
            if (seconds > 0) {
                return jedis.setex(key, seconds, value);
            } else {
                return jedis.set(key, value);
            }
        } catch (Exception e) {
            log.error("redis setString failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }


    /**
     * 向redis中存入数据
     *
     * @param key    键值
     * @param object 数据
     * @return
     */
    public <T> boolean set(String key, T object) {
        return set(key, object, -1);
    }

    /**
     * 向redis中存入数据
     *
     * @param key    键值
     * @param object 数据
     * @return
     */
    public <T> boolean set(String key, T object, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if (seconds > 0) {
                jedis.setex(key.getBytes(), seconds, ConvertUtil.serialize(object));
            } else {
                jedis.set(key.getBytes(), ConvertUtil.serialize(object));
            }
        } catch (Exception e) {
            log.error("redis set failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }


    /**
     * @param key     键值
     * @param value
     * @param seconds
     * @return
     */
    public boolean setnxString(String key, String value, int seconds) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            Long res = jedis.setnx(key, value);
            if (new Long(1L).equals(res)) {
                // 设定过期时间，最多30秒自动过期，防止长期死锁发生
                jedis.expire(key, seconds);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("redis setnxString failed!", e);
            return false;
        } finally {
            close(jedis);
        }
    }


    /**
     * 指定的 key 不存在时，为 key 设置指定的值。
     * 设置key value,如果key已经存在则返回0,nx==> not exist
     *
     * @param key key
     * @return true:存在，false:不存在
     */
    public boolean setnx(String key, Object object) {
        return setnx(key, object, 30);
    }

    /**
     * 指定的 key 不存在时，为 key 设置指定的值。
     *
     * @param key     键值
     * @param object
     * @param seconds
     * @return true:存在，false:不存在
     */
    public boolean setnx(String key, Object object, int seconds) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            Long res = jedis.setnx(key, object.toString());
            if (new Long(1L).equals(res)) {
                // 设定过期时间，最多30秒自动过期，防止长期死锁发生
                jedis.expire(key.getBytes(), seconds);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("redis setnx failed!", e);
            return false;
        } finally {
            close(jedis);
        }
    }

    /**
     * @param key     键值
     * @param value
     * @param seconds
     * @return
     */
    public String setexString(String key, String value, int seconds) {
        Jedis jedis = null;
        try {
            value = StringUtils.isEmpty(value) ? "" : value;
            jedis = getJedis();
            return jedis.setex(key, seconds, value);

        } catch (Exception e) {
            log.error("redis setexString failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 通过key 和offset 从指定的位置开始将原先value替换
     * 下标从0开始,offset表示从offset下标开始替换
     * 如果替换的字符串长度过小则会这样
     * example:
     * value : bigsea@zto.cn
     * str : abc
     * 从下标7开始替换  则结果为
     * RES : bigsea.abc.cn
     *
     * @param key    键值
     * @param str
     * @param offset 下标位置
     * @return 返回替换后  value 的长度
     */
    public Long setrange(String key, String str, int offset) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.setrange(key, offset, str);
        } catch (Exception e) {
            log.error("redis setrange failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }


    /**
     * 通过批量的key获取批量的value
     *
     * @param keys string数组 也可以是一个key
     * @return 成功返回value的集合, 失败返回null的集合 ,异常返回空
     */
    public List<String> mget(String... keys) {
        Jedis jedis = null;
        List<String> values = null;
        try {
            jedis = getJedis();
            values = jedis.mget(keys);
        } catch (Exception e) {
            log.error("redis mget failed!", e);
        } finally {
            close(jedis);
        }
        return values;
    }

    /**
     * 批量的设置key:value,可以一个
     * example:
     * obj.mset(new String[]{"key2","value1","key2","value2"})
     *
     * @param keysvalues
     * @return 成功返回OK 失败 异常 返回 null
     */
    public String mset(String... keysvalues) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.mset(keysvalues);
        } catch (Exception e) {
            log.error("redis mset failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 批量的设置key:value,可以一个,如果key已经存在则会失败,操作会回滚
     * example:
     * obj.msetnx(new String[]{"key2","value1","key2","value2"})
     *
     * @param keysvalues
     * @return 成功返回1 失败返回0
     */
    public Long msetnx(String... keysvalues) {
        Jedis jedis = null;
        Long res = 0L;
        try {
            jedis = getJedis();
            res = jedis.msetnx(keysvalues);
        } catch (Exception e) {
            log.error("redis msetnx failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key向指定的value值追加值
     *
     * @param key   键值
     * @param value
     * @return 成功返回 添加后value的长度 失败 返回 添加的 value 的长度  异常返回0L
     */
    public long append(String key, String value) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.append(key, value);
        } catch (Exception e) {
            log.error("redis append failed!", e);
            return -1;
        } finally {
            close(jedis);
        }
    }

    /**
     * 通过key获取value值的长度
     *
     * @param key 键值
     * @return 失败返回null
     */
    public Long strlen(String key) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.strlen(key);
        } catch (Exception e) {
            log.error("redis strlen failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 自增
     *
     * @param key key
     * @return 0:失败，非0:成功
     */
    public Long incr(String key) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.incr(key);
        } catch (Exception e) {
            log.error("redis incr failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }

    /**
     * 通过key给指定的value加值,如果key不存在,则这是value为该值
     *
     * @param key  键值
     * @param incr
     * @return
     */
    public Long incrBy(String key, Long incr) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.incrBy(key, incr);
        } catch (Exception e) {
            log.error("redis incrBy failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }

    /**
     * 自减
     *
     * @param key key
     * @return 0:失败，非0:成功
     */
    public Long decr(String key) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.decr(key);
        } catch (Exception e) {
            log.error("redis decr failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }

    /**
     * 自减
     *
     * @param key  键值
     * @param decr
     * @return
     */
    public Long decrBy(String key, Long decr) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.decrBy(key, decr);
        } catch (Exception e) {
            log.error("redis decrBy failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }

    /**
     * 添加数据到Set里面
     * 添加一条数据，返回添加的新元素的数量
     * 0代表已经有重复，1代表添加成功一条没有的记录
     *
     * @param key   键值
     * @param value 可以是一个String 也可以是一个String数组
     * @return 添加成功的个数
     */
    public long sadd(String key, String... value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            jedis.pipelined();

            return jedis.sadd(key, value);

        } catch (Exception e) {
            log.error("redis sadd failed!", e);
            return -1;
        } finally {
            close(jedis);
        }
    }

    /**
     * 使用管道批量操作数据，自动过滤空值
     *
     * @param key        键值
     * @param listValues
     * @return
     */
    public boolean saddPipeLine(String key, List<String> listValues) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            Pipeline p = jedis.pipelined();
            for (String item : listValues) {
                if (StringUtils.isEmpty(item)) {
                    continue;
                }
                p.sadd(key, item);
            }
            p.sync();

        } catch (Exception e) {
            log.error("redis saddPipeLine failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }

    /**
     * 批量添加Object对象
     *
     * @param key        键值
     * @param listValues
     * @return
     */
    public boolean saddPipeLineObj(String key, List<Object> listValues) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            Pipeline p = jedis.pipelined();
            for (Object item : listValues) {
                if (null == item) {
                    continue;
                }
                p.sadd(key, JSON.toJSONString(item));
            }
            p.sync();

        } catch (Exception e) {
            log.error("redis saddPipeLine failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }

    /**
     * 获取Set所有数据,通过key获取set中所有的value
     *
     * @param key 键值
     * @return
     */
    public Set<String> smembers(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            Set<String> listData = jedis.smembers(key);

            return listData;

        } catch (Exception e) {
            log.error("redis smembers failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 判断member是否在set里面,通过key判断value是否是set中的元素
     *
     * @param key    键值
     * @param member
     * @return
     */
    public boolean sismember(String key, String member) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            return jedis.sismember(key, member);

        } catch (Exception e) {
            log.error("redis sismember failed!", e);
            return false;
        } finally {
            close(jedis);
        }
    }

    /**
     * 通过key随机删除一个set中的value并返回该值
     *
     * @param key 键值
     * @return
     */
    public String spop(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            return jedis.spop(key);

        } catch (Exception e) {
            log.error("redis spop failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 随机获取键名下面的一个结果
     * 通过key获取set中随机的value,不删除元素
     *
     * @param key 键值
     * @return
     */
    public String srandmember(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            return jedis.srandmember(key);

        } catch (Exception e) {
            log.error("redis srandmember failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }


    /**
     * 把set中的数据移动到另一个set中,
     * 如果descKey不存在，会把member插入进去
     *
     * @param srcKey  需要移除的
     * @param descKey 添加的
     * @param member  set中的value
     * @return
     */
    public long smove(String srcKey, String descKey, String member) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            if (jedis.exists(descKey)) {
                return jedis.smove(srcKey, descKey, member);
            } else {
                return jedis.sadd(descKey, member);
            }

        } catch (Exception e) {
            log.error("redis smove failed!", e);
            return -1;
        } finally {
            close(jedis);
        }
    }

    /**
     * 通过key删除set中对应的value值
     *
     * @param key     键值
     * @param members 可以是一个String 也可以是一个String数组
     * @return 删除的个数
     */
    public long srem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = getJedis();

            return jedis.srem(key, members);

        } catch (Exception e) {
            log.error("redis srem failed!", e);
            return -1;
        } finally {
            close(jedis);
        }
    }


    /**
     * 通过key获取set中value的个数
     *
     * @param key 键值
     * @return
     */
    public Long scard(String key) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.scard(key);
        } catch (Exception e) {
            log.error("redis scard failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key获取set中的差集
     * 以第一个set为标准
     *
     * @param keys 可以使一个string 则返回set中所有的value 也可以是string数组
     * @return
     */
    public Set<String> sdiff(String... keys) {
        Jedis jedis = null;
        Set<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.sdiff(keys);
        } catch (Exception e) {
            log.error("redis sdiff failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key获取set中的差集并存入到另一个key中
     * 以第一个set为标准
     *
     * @param dstkey 差集存入的key
     * @param keys   可以使一个string 则返回set中所有的value 也可以是string数组
     * @return
     */
    public Long sdiffstore(String dstkey, String... keys) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.sdiffstore(dstkey, keys);
        } catch (Exception e) {
            log.error("redis sdiffstore failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key获取指定set中的交集
     *
     * @param keys 可以使一个string 也可以是一个string数组
     * @return
     */
    public Set<String> sinter(String... keys) {
        Jedis jedis = null;
        Set<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.sinter(keys);
        } catch (Exception e) {
            log.error("redis sinter failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key获取指定set中的交集 并将结果存入新的set中
     *
     * @param dstkey
     * @param keys   可以使一个string 也可以是一个string数组
     * @return
     */
    public Long sinterstore(String dstkey, String... keys) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.sinterstore(dstkey, keys);
        } catch (Exception e) {
            log.error("redis sinterstore failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key返回所有set的并集
     *
     * @param keys 可以使一个string 也可以是一个string数组
     * @return
     */
    public Set<String> sunion(String... keys) {
        Jedis jedis = null;
        Set<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.sunion(keys);
        } catch (Exception e) {
            log.error("redis sunion failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key返回所有set的并集,并存入到新的set中
     *
     * @param dstkey
     * @param keys   可以使一个string 也可以是一个string数组
     * @return
     */
    public Long sunionstore(String dstkey, String... keys) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.sunionstore(dstkey, keys);
        } catch (Exception e) {
            log.error("redis sunionstore failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key返回所有的field
     *
     * @param key 键值
     * @return
     */
    public Set<String> hkeys(String key) {
        Jedis jedis = null;
        Set<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.hkeys(key);
        } catch (Exception e) {
            log.error("redis hkeys failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key返回所有和key有关的value
     *
     * @param key 键值
     * @return
     */
    public List<String> hvals(String key) {
        Jedis jedis = null;
        List<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.hvals(key);
        } catch (Exception e) {
            log.error("redis hvals failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key获取所有的field和value
     *
     * @param key 键值
     * @return
     */
    public Map<String, String> hgetall(String key) {
        Jedis jedis = null;
        Map<String, String> res = null;
        try {
            jedis = getJedis();
            res = jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("redis hgetall failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key 和 field 获取指定的 value
     *
     * @param key   键值
     * @param field
     * @return 没有返回null
     */
    public String hget(String key, String field) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.hget(key, field);
        } catch (Exception e) {
            log.error("redis hget failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 获取数据
     *
     * @param key       键值
     * @param fieldName
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T hget(String key, String fieldName, Class<T> clazz) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return ConvertUtil.unserialize(jedis.hget(key.getBytes(), fieldName.getBytes()), clazz);
        } catch (Exception e) {
            log.error("redis hget failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }


    /**
     * 通过key 和 fields 获取指定的value 如果没有对应的value则返回null
     *
     * @param key    键值
     * @param fields 可以使 一个String 也可以是 String数组
     * @return
     */
    public List<String> hmget(String key, String... fields) {
        Jedis jedis = null;
        List<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.hmget(key, fields);
        } catch (Exception e) {
            log.error("redis hmget failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key给field设置指定的值,如果key不存在,则先创建
     *
     * @param key   键值
     * @param field 字段
     * @param value
     * @return 如果存在返回0 异常返回null
     */
    public Long hset(String key, String field, String value) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.hset(key, field, value);
        } catch (Exception e) {
            log.error("redis hset failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 向redis中存入数据
     *
     * @param key    键值
     * @param object 数据
     * @return
     */
    public boolean hset(String key, String fieldName, Object object) {
        return hset(key, fieldName, object, -1);
    }

    /**
     * 向redis中存入数据
     *
     * @param key    键值
     * @param object 数据
     * @return
     */
    public <T> boolean hset(String key, String fieldName, T object, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hset(key.getBytes(), fieldName.getBytes(), ConvertUtil.serialize(object));
            if (seconds > 0) {
                jedis.expire(key.getBytes(), seconds);
            }
        } catch (Exception e) {
            log.error("redis hset failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }


    /**
     * 通过key给field设置指定的值,如果key不存在则先创建,如果field已经存在,返回0
     *
     * @param key   键值
     * @param field
     * @param value
     * @return
     */
    public Long hsetnx(String key, String field, String value) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.hsetnx(key, field, value);
        } catch (Exception e) {
            log.error("redis hsetnx failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key同时设置 hash的多个field
     *
     * @param key  键值
     * @param hash
     * @return 返回OK 异常返回null
     */
    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.hmset(key, hash);
        } catch (Exception e) {
            log.error("redis hmset failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 向redis中存入数据
     *
     * @param key       键值
     * @param fieldName 数据
     * @return
     */
    public <T> boolean hdel(String key, String fieldName) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.hdel(key.getBytes(), fieldName.getBytes());
        } catch (Exception e) {
            log.error("redis hdel failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }


    /**
     * 通过key 删除指定的 field
     *
     * @param key    键值
     * @param fields 可以是 一个 field 也可以是 一个数组
     * @return
     */
    public Long hdel(String key, String... fields) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.hdel(key, fields);
        } catch (Exception e) {
            log.error("redis hdel failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key给指定的field的value加1
     *
     * @param key   键值
     * @param field 字段
     * @return
     */
    public Long hincrby(String key, String field) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.hincrBy(key, field, 1);
        } catch (Exception e) {
            log.error("redis hincrby failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key给指定的field的value加上给定的值
     *
     * @param key   键值
     * @param field
     * @param value
     * @return
     */
    public Long hincrby(String key, String field, Long value) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.hincrBy(key, field, value);
        } catch (Exception e) {
            log.error("redis hincrby failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key和field判断是否有指定的value存在
     *
     * @param key   键值
     * @param field
     * @return
     */
    public Boolean hexists(String key, String field) {
        Jedis jedis = null;
        Boolean res = false;
        try {
            jedis = getJedis();
            res = jedis.hexists(key, field);
        } catch (Exception e) {
            log.error("redis hexists failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key返回field的数量
     *
     * @param key 键值
     * @return
     */
    public Long hlen(String key) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.hlen(key);
        } catch (Exception e) {
            log.error("redis hlen failed!", e);
        } finally {
            close(jedis);
        }
        return res;

    }

    /**
     * 通过key向list头部添加字符串
     *
     * @param key  键值
     * @param strs 可以使一个string 也可以使string数组
     * @return 返回list的value个数
     */
    public Long lpush(String key, String... strs) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.lpush(key, strs);
        } catch (Exception e) {
            log.error("redis lpush failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 向redis中存入列表
     *
     * @param key    键值
     * @param object 数据
     * @return
     */
    public <T> boolean lpush(String key, T object) {
        Long ret = lpush(key, object, 0);
        if (ret > 0) {
            return true;
        }
        return false;
    }

    /**
     * 存储REDIS队列 顺序存储,可设置过期时间，过期时间以秒为单位
     *
     * @param key    reids键名
     * @param value  键值
     * @param second 过期时间(秒)
     */
    public <T> Long lpush(String key, T value, int second) {
        Jedis jedis = null;
        Long ret = null;
        try {
            jedis = getJedis();
            byte[] bytes = ConvertUtil.serialize(value);
            ret = jedis.lpush(key.getBytes(), bytes);

            if (second > 0) {
                jedis.expire(key, second);
            }

        } catch (Exception e) {
            log.error("redis lpush failed , key = " + key, e);
        } finally {
            close(jedis);
        }

        return ret;
    }

    /**
     * @param key    键值
     * @param value
     * @param second
     */
    public void lpushStr(String key, String value, int second) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.lpush(key, value);

            if (second > 0) {
                jedis.expire(key, second);
            }
        } catch (Exception e) {
            log.error("redis lpushStr failed , key = " + key, e);
        } finally {
            close(jedis);
        }
    }


    /**
     * 通过key向list尾部添加字符串
     *
     * @param key  键值
     * @param strs 可以使一个string 也可以使string数组
     * @return 返回list的value个数
     */
    public Long rpush(String key, String... strs) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.rpush(key, strs);
        } catch (Exception e) {
            log.error("redis rpush failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key获取list指定下标位置的value
     * 如果start 为 0 end 为 -1 则返回全部的list中的value
     *
     * @param key   键值
     * @param start
     * @param end
     * @return
     */
    public List<String> lrange(String key, long start, long end) {
        Jedis jedis = null;
        List<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error("redis lrange failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 从列表中后去元素
     *
     * @param key   键值
     * @param clazz
     * @return
     */
    public <T> List<T> lrange(String key, int start, int end, Class<T> clazz) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return ConvertUtil.unserialize(jedis.lrange(key.getBytes(), start, end), clazz);
        } catch (Exception e) {
            log.error("redis lrange data failed , key = " + key, e);
        } finally {
            close(jedis);
        }

        return new ArrayList<T>();
    }


//    /**
//     * 通过key在list指定的位置之前或者之后 添加字符串元素
//     *
//     * @param key   键值
//     * @param where LIST_POSITION枚举类型
//     * @param pivot list里面的value
//     * @param value 添加的value
//     * @return
//     */
//    public Long linsert(String key, BinaryClient.LIST_POSITION where,
//                        String pivot, String value) {
//        Jedis jedis = null;
//        Long res = null;
//        try {
//            jedis = getJedis();
//            res = jedis.linsert(key, where, pivot, value);
//        } catch (Exception e) {
//            log.error("redis linsert failed!", e);
//        } finally {
//            close(jedis);
//        }
//        return res;
//    }

    /**
     * 通过key设置list指定下标位置的value
     * 如果下标超过list里面value的个数则报错
     *
     * @param key   键值
     * @param index 从0开始
     * @param value
     * @return 成功返回OK
     */
    public String lset(String key, Long index, String value) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.lset(key, index, value);
        } catch (Exception e) {
            log.error("redis lset failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 通过key从对应的list中删除指定的count个 和 value相同的元素
     *
     * @param key   键值
     * @param count 当count为0时删除全部
     * @param value
     * @return 返回被删除的个数
     */
    public Long lrem(String key, long count, String value) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.lrem(key, count, value);
        } catch (Exception e) {
            log.error("redis lrem failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key保留list中从strat下标开始到end下标结束的value值
     *
     * @param key   键值
     * @param start
     * @param end
     * @return 成功返回OK
     */
    public String ltrim(String key, long start, long end) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.ltrim(key, start, end);
        } catch (Exception e) {
            log.error("redis ltrim failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key从list的头部删除一个value,并返回该value
     *
     * @param key 键值
     * @return
     */
    public String lpop(String key) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.lpop(key);
        } catch (Exception e) {
            log.error("redis lpop failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key从list尾部删除一个value,并返回该元素
     *
     * @param key 键值
     * @return
     */
    public String rpop(String key) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.rpop(key);
        } catch (Exception e) {
            log.error("redis rpop failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key从一个list的尾部删除一个value并添加到另一个list的头部,并返回该value
     * 如果第一个list为空或者不存在则返回null
     *
     * @param srckey
     * @param dstkey
     * @return
     */
    public String rpoplpush(String srckey, String dstkey) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.rpoplpush(srckey, dstkey);
        } catch (Exception e) {
            log.error("redis rpoplpush failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key获取list中指定下标位置的value
     *
     * @param key   键值
     * @param index
     * @return 如果没有返回null
     */
    public String lindex(String key, long index) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = getJedis();
            res = jedis.lindex(key, index);
        } catch (Exception e) {
            log.error("redis lindex failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key返回list的长度
     *
     * @param key 键值
     * @return
     */
    public Long llen(String key) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.llen(key);
        } catch (Exception e) {
            log.error("redis llen failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 对有序集合中指定成员的分数加上增量 increment
     *
     * @param key    键值
     * @param score
     * @param object
     */
    public boolean zincrby(String key, double score, Object object) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.zincrby(key, score, object.toString());
        } catch (Exception e) {
            log.error("redis zincrby failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }


    /**
     * 通过key向zset中添加value,score,其中score就是用来排序的
     * 如果该value已经存在则根据score更新元素
     *
     * @param key          键值
     * @param scoreMembers
     * @return
     */
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.zadd(key, scoreMembers);
        } catch (Exception e) {
            log.error("redis zadd failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key向zset中添加value,score,其中score就是用来排序的
     * 如果该value已经存在则根据score更新元素
     *
     * @param key   键值
     * @param score 评分
     * @param val
     * @return
     */
    public Long zadd(String key, double score, String val) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.zadd(key, score, val);
        } catch (Exception e) {
            log.error("redis zadd failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 向有序集合添加元素
     *
     * @param key    键值
     * @param score
     * @param object
     */
    public boolean zadd(String key, double score, Object object) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.zadd(key, score, object.toString());
        } catch (Exception e) {
            log.error("redis zadd data failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }

    /**
     * 向有序集合添加元素
     *
     * @param key          键值
     * @param scoreMembers
     * @param seconds
     */
    public boolean zadd(String key, Map<String, Double> scoreMembers, int seconds) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.zadd(key, scoreMembers);
            if (seconds > 0) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            log.error("redis zadd data failed!", e);
            return false;
        } finally {
            close(jedis);
        }
        return true;
    }


    /**
     * 通过key增加该zset中value的score的值
     *
     * @param key    键值
     * @param score
     * @param member
     * @return
     */
    public Double zincrby(String key, double score, String member) {
        Jedis jedis = null;
        Double res = null;
        try {
            jedis = getJedis();
            res = jedis.zincrby(key, score, member);
        } catch (Exception e) {
            log.error("redis zincrby failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 返回指定区间内zset中value的数量
     *
     * @param key 键值
     * @param min
     * @param max
     * @return
     */
    public Long zcount(String key, String min, String max) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.zcount(key, min, max);
        } catch (Exception e) {
            log.error("redis zcount failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }


    /**
     * 计算在有序集合中指定区间分数的成员数
     *
     * @param key 键值
     * @return
     */
    public Long zcount(String key, double minScore, double maxScore) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.zcount(key, minScore, maxScore);
        } catch (Exception e) {
            log.error("redis zcount failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }

    /**
     * 通过key返回zset中的value个数
     *
     * @param key 键值
     * @return
     */
    public Long zcard(String key) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.zcard(key);
        } catch (Exception e) {
            log.error("redis zcard failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key获取zset中value的score值
     *
     * @param key    键值
     * @param member
     * @return
     */
    public Double zscore(String key, String member) {
        Jedis jedis = null;
        Double res = null;
        try {
            jedis = getJedis();
            res = jedis.zscore(key, member);
        } catch (Exception e) {
            log.error("redis zscore failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 移除有序列表成员,通过key删除在zset中指定的value
     *
     * @param key     键值
     * @param members 待移除的成员
     * @return
     * @category 移除有序列表成员
     */
    public Long zrem(String key, String... members) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.zrem(key, members);
        } catch (Exception e) {
            log.error("redis zrem failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }


    /**
     * 通过key返回zset中value的排名
     * 下标从小到大排序
     *
     * @param key    键值
     * @param member
     * @return
     */
    public Long zrank(String key, String member) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.zrank(key, member);
        } catch (Exception e) {
            log.error("redis zrank failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key返回zset中value的排名
     * 下标从大到小排序
     *
     * @param key    键值
     * @param member
     * @return
     */
    public Long zrevrank(String key, String member) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.zrevrank(key, member);
        } catch (Exception e) {
            log.error("redis zrevrank failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key将获取score从start到end中zset的value
     * socre从大到小排序
     * 当start为0 end为-1时返回全部
     *
     * @param key   键值
     * @param start
     * @param end
     * @return
     */
    public Set<String> zrevrange(String key, long start, long end) {
        Jedis jedis = null;
        Set<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.zrevrange(key, start, end);
        } catch (Exception e) {
            log.error("redis zrevrange failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 通过key删除给定区间内的元素
     *
     * @param key   键值
     * @param start
     * @param end
     * @return
     */
    public Long zremrangeByRank(String key, long start, long end) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = getJedis();
            res = jedis.zremrangeByRank(key, start, end);
        } catch (Exception e) {
            log.error("redis zremrangeByRank failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 于移除有序集中，指定分数（score）区间内的所有成员
     *
     * @param key      键值
     * @param maxScore 最小分数（包括）
     * @param minScore 最大分数（包括）
     * @return 被移除成员的数量
     */
    public Long zremrangeByScore(String key, double maxScore, double minScore) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.zremrangeByScore(key, maxScore, minScore);
        } catch (Exception e) {
            log.error("redis zremrangeByScore failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }

    /**
     * 通过有序列表中值移除元素
     *
     * @param key      键值
     * @param minValue 包括
     * @param maxValue 包括
     * @return
     */
    public Long zremrangeByLex(String key, String minValue, String maxValue) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.zremrangeByLex(key, "[" + minValue, "[" + maxValue);
        } catch (Exception e) {
            log.error("redis zremrangeByLex failed!", e);
            return 0L;
        } finally {
            close(jedis);
        }
    }

    /**
     * 有序集所有成员列表。（从小--->大）
     *
     * @param key    键值
     * @param offset 偏移量
     * @param count  总数
     * @return 有序集成员的列表
     */
    public Set<String> zrangeAll(String key, int offset, int count) {
        return zrangeByScore(key, "-inf", "+inf", offset, count);
    }

    /**
     * 有序集所有成员列表。（从小--->大）
     *
     * @param key 键值
     * @return 有序集成员的列表
     */
    public Set<String> zrangeAll(String key) {
        return zrangeByScore(key, "-inf", "+inf");
    }

    /**
     * 有序集所有成员列表。（从小--->大）
     *
     * @param key 键值
     * @return
     */
    public Set<Tuple> zrangeAllWithScore(String key) {
        return zrangeByScoreWithScore(key, "-inf", "+inf");
    }

    /**
     * 大于等于最小分数的有序集成员列表。（从小--->大）
     *
     * @param key      键值
     * @param minScore 最小分数（包括），用'-inf'标识正无穷
     * @return 有序集成员的列表
     */
    public Set<String> zrangeByScoreGreaterOrEqual(String key, String minScore) {
        return zrangeByScore(key, minScore, "+inf");
    }

    /**
     * 大于等于最小分数的有序集成员列表。（从小--->大）
     *
     * @param key      键值
     * @param minScore 最小分数（包括），用'-inf'标识正无穷
     * @return 有序集成员的列表
     */
    public Set<String> zrangeByScoreGreaterOrEqual(String key, String minScore, int offset, int count) {
        return zrangeByScore(key, minScore, "+inf", offset, count);
    }

    /**
     * 小于等于最大分数的有序集成员列表。（从小--->大）
     *
     * @param key      键值
     * @param maxScore 最大分数（包括），用'+inf'标识正无穷
     * @return 有序集成员的列表
     */
    public Set<String> zrangeByScoreLessOrEqual(String key, String maxScore) {
        return zrangeByScore(key, "-inf", maxScore);
    }

    /**
     * 小于等于最大分数的有序集成员列表。（从小--->大）
     *
     * @param key      键值
     * @param maxScore 最大分数（包括），用'+inf'标识正无穷
     * @param offset   偏移量
     * @param count    返回总数
     * @return 有序集成员的列表
     */
    public Set<String> zrangeByScoreLessOrEqual(String key, String maxScore, int offset, int count) {
        return zrangeByScore(key, "-inf", maxScore, offset, count);
    }

    /**
     * 指定区间内，有序集成员的列表。（从小--->大）
     *
     * @param key      键值
     * @param minScore 最小分数（包括），用'-inf'表示负无穷
     * @param maxScore 最大分数（包括），用'+inf'标识正无穷
     * @return 有序集成员的列表
     */
    public Set<String> zrangeByScore(String key, String minScore, String maxScore) {
        return zrangeByScore(key, minScore, maxScore, -1, -1);
    }

    /**
     * 指定区间内，有序集成员的列表。（从小--->大）
     *
     * @param key
     * @param minScore
     * @param maxScore
     * @return
     */
    public Set<Tuple> zrangeByScoreWithScore(String key, String minScore, String maxScore) {
        return zrangeByScoreWithScore(key, minScore, maxScore, -1, -1);
    }

    /**
     * 指定区间内，有序集成员的列表。（从小--->大）
     *
     * @param key      键值
     * @param minScore 最小分数（包括），用'-inf'表示负无穷
     * @param maxScore 最大分数（包括），用'+inf'标识正无穷
     * @param offset   偏移量
     * @param count    返回总数
     * @return 有序集成员的列表
     */
    public Set<String> zrangeByScore(String key, String minScore, String maxScore, int offset, int count) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            if (offset > -1 && count > 0) {
                return jedis.zrangeByScore(key, minScore, maxScore, offset, count);
            } else {
                return jedis.zrangeByScore(key, minScore, maxScore);
            }
        } catch (Exception e) {
            log.error("redis zrangeByScore failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 取出带得分的Score
     *
     * @param key
     * @param minScore
     * @param maxScore
     * @param offset
     * @param count
     * @return
     */
    public Set<Tuple> zrangeByScoreWithScore(String key, String minScore, String maxScore, int offset, int count) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            if (offset > -1 && count > 0) {
                return jedis.zrangeByScoreWithScores(key, minScore, maxScore, offset, count);
            } else {
                return jedis.zrangeByScoreWithScores(key, minScore, maxScore);
            }
        } catch (Exception e) {
            log.error("redis zrangeByScoreWithScore failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 有序集所有成员列表。（从大--->小）
     *
     * @param key    键值
     * @param offset 偏移量
     * @param count  总数
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeAll(String key, int offset, int count) {
        return zrevrangeByScore(key, "+inf", "-inf", offset, count);
    }

    /**
     * 有序集所有成员列表。（从大--->小）
     *
     * @param key 键值
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeAll(String key) {
        return zrevrangeByScore(key, "+inf", "-inf");
    }

    /**
     * 大于等于最小分数的有序集成员列表。（从大--->小）
     *
     * @param key      键值
     * @param minScore 最小分数（包括），用'-inf'标识正无穷
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeByScoreGreaterOrEqual(String key, String minScore) {
        return zrevrangeByScore(key, "+inf", minScore);
    }

    /**
     * 大于等于最小分数的有序集成员列表。（从大--->小）
     *
     * @param key      键值
     * @param minScore 最小分数（包括），用'-inf'标识正无穷
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeByScoreGreaterOrEqual(String key, String minScore, int offset, int count) {
        return zrevrangeByScore(key, "+inf", minScore, offset, count);
    }

    /**
     * 小于等于最大分数的有序集成员列表。（从大--->小）
     *
     * @param key      键值
     * @param maxScore 最大分数（包括），用'+inf'标识正无穷
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeByScoreLessOrEqual(String key, String maxScore) {
        return zrevrangeByScore(key, maxScore, "-inf");
    }

    /**
     * 小于等于最大分数的有序集成员列表。（从大--->小）
     *
     * @param key      键值
     * @param maxScore 最大分数（包括），用'+inf'标识正无穷
     * @param offset   偏移量
     * @param count    返回总数
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeByScoreLessOrEqual(String key, String maxScore, int offset, int count) {
        return zrevrangeByScore(key, maxScore, "-inf", offset, count);
    }

    /**
     * 指定区间内，有序集成员的列表。（从大--->小）
     *
     * @param key      键值
     * @param maxScore 最小分数（包括），用'+inf'表示负无穷
     * @param minScore 最大分数（包括），用'-inf'标识正无穷
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeByScore(String key, String maxScore, String minScore) {
        return zrevrangeByScore(key, maxScore, minScore, -1, -1);
    }


    /**
     * 通过key返回指定score内zset中的value
     *
     * @param key 键值
     * @param max
     * @param min
     * @return
     */
    public Set<String> zrangebyscore(String key, String max, String min) {
        Jedis jedis = null;
        Set<String> res = null;
        try {
            jedis = getJedis();
            res = jedis.zrevrangeByScore(key, max, min);
        } catch (Exception e) {
            log.error("redis zrangebyscore failed!", e);
        } finally {
            close(jedis);
        }
        return res;
    }

    /**
     * 指定区间内，有序集成员的列表。（从小--->大）
     * 通过key返回指定score内zset中的value
     *
     * @param key      键值
     * @param minScore 最小分数（包括）
     * @param maxScore 最大分数（包括）
     * @return 有序集成员的列表
     */
    public Set<String> zrangeByScore(String key, double minScore, double maxScore) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.zrangeByScore(key, minScore, maxScore);
        } catch (Exception e) {
            log.error("redis zrangeByScore failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 指定区间内，有序集成员的列表。（从大--->小）
     *
     * @param key      键值
     * @param maxScore 最小分数（包括），用'+inf'表示负无穷
     * @param minScore 最大分数（包括），用'-inf'标识正无穷
     * @param offset   偏移量
     * @param count    返回总数
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeByScore(String key, String maxScore, String minScore, int offset, int count) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            if (offset > -1 && count > 0) {
                return jedis.zrevrangeByScore(key, maxScore, minScore, offset, count);
            } else {
                return jedis.zrangeByScore(key, maxScore, minScore);
            }
        } catch (Exception e) {
            log.error("redis zrevrangeByScore failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 指定区间内，有序集成员的列表。（从大--->小）
     *
     * @param key      键值
     * @param maxScore 最大分数（包括）
     * @param minScore 最小分数（包括）
     * @return 有序集成员的列表
     */
    public Set<String> zrevrangeByScore(String key, double maxScore, double minScore) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.zrevrangeByScore(key, maxScore, minScore);
        } catch (Exception e) {
            log.error("redis zrevrangeByScore failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 指定区间内，有序集成员的列表。（从大--->小）
     *
     * @param key      键值
     * @param maxScore 最大分数（包括）
     * @param minScore 最小分数（包括）
     * @return
     */
    public Set<Tuple> zrevrangeByScoreWithScore(String key, double maxScore, double minScore) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.zrevrangeByScoreWithScores(key, maxScore, minScore);
        } catch (Exception e) {
            log.error("redis zrevrangeByScore failed!", e);
            return null;
        } finally {
            close(jedis);
        }
    }

    /**
     * 保存数据 类型为 Map
     *
     * @param key     键值
     * @param mapData
     */
    public String setMapData(String key, Map<String, String> mapData) {
        Jedis jedis = null;

        try {
            jedis = getJedis();
            return jedis.hmset(key, mapData);
        } catch (Exception e) {
            log.error("redis set map data failed! map = " + mapData, e);
        } finally {
            close(jedis);
        }

        return null;
    }

    /**
     * 获取Map数据
     *
     * @param key 键值
     * @return
     */
    public Map<String, String> getMapData(String key) {
        Map<String, String> dataMap = null;
        Jedis jedis = null;

        try {
            jedis = getJedis();
            dataMap = jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("redis get map data failed! ", e);
        } finally {
            close(jedis);
        }
        return dataMap;
    }


    /**
     * 去锁
     *
     * @param key 键值
     * @return
     */
    public boolean getLock(String key) {
        return setnx(key + "_lock", "");
    }

    /**
     * 释放锁
     *
     * @param key 键值
     * @return
     */
    public boolean releaseLock(String key) {
        return delete(key + "_lock");
    }

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "EX";

//    /**
//     * 获取分布式锁
//     * nxxx的值只能取NX或者XX，如果取NX，则只有当key不存在是才进行set，如果取XX，则只有当key已经存在时才进行set<br>expx的值只能取EX或者PX，代表数据过期时间的单位，EX代表秒，PX代表毫秒。<br>time 过期时间，单位是expx所代表的单位。
//     *
//     * @param lockKey    锁
//     * @param requestId  请求标识
//     * @param expireTime 超期时间
//     * @return 是否获取成功
//     */
//    public boolean tryGetDistributedLock(String lockKey, String requestId, int expireTime) {
//        Jedis jedis = null;
//        try {
//            jedis = getJedis();
//
//            SetParams setParams=new SetParams();
//            String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
//            if (LOCK_SUCCESS.equals(result)) {
//                return true;
//            }
//            return false;
//        } finally {
//            close(jedis);
//        }
//    }

}