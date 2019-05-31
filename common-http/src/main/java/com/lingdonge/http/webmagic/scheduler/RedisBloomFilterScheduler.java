package com.lingdonge.http.webmagic.scheduler;

import com.alibaba.fastjson.JSON;
import com.lingdonge.core.encrypt.Md5Util;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.http.webmagic.PageType;
import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.scheduler.component.DuplicateRemover;
import orestes.bloomfilter.FilterBuilder;
import orestes.bloomfilter.HashProvider;
import orestes.bloomfilter.redis.BloomFilterRedis;
import orestes.bloomfilter.redis.helper.RedisPool;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisBloomFilterScheduler
 * 基于Redis的BloomFilter过滤器，可以过滤上亿的数据集
 * 队列在queue_**.com下面，Bloom做重复过滤在bloomfilter_下面，旧的set过滤已经被淘汰
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public class RedisBloomFilterScheduler extends DuplicateRemovedScheduler implements MonitorableScheduler, DuplicateRemover {

    protected JedisPool pool;

    // 待抓取URL队列
    private static final String QUEUE_PREFIX = "queue_";

    // 抓取所需的Request队列，将从里面取出Request信息进行抓取，一般存储的是细化到每条抓取任务的细节信息
    private static final String ITEM_PREFIX = "item_";

    private static String regexMatchUrl = "";

    private static BloomFilterRedis<String> bloomFilter;//Bloom过滤器

    private static String bloomRedisKey;// Bloom在Redis里面的Key值

    /**
     * @param config
     * @param redisKey
     */
    public RedisBloomFilterScheduler(RedisProperties config, String redisKey) {
        this(config, "", redisKey);
    }

    /**
     * 可以对满足条件的URL移动到队列左侧
     *
     * @param regexMatchUrl 正则列表，满足条件的将会移动此URL到队列左侧
     * @param redisKey
     */
    public RedisBloomFilterScheduler(RedisProperties config, String regexMatchUrl, String redisKey) {

        if (StringUtils.isNotEmpty(regexMatchUrl)) {
            logger.info("将会使用字符串：[" + regexMatchUrl + "]判断URL是否添加到队列左侧");
        }

        this.pool = new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort(), 3000, config.getPassword());
        this.regexMatchUrl = regexMatchUrl;
        this.bloomRedisKey = "bloomfilter_" + redisKey;

        setDuplicateRemover(this);

        bloomFilter = new BloomFilterRedis<String>(new FilterBuilder(10_000_000, 0.000001)
                .hashFunction(HashProvider.HashMethod.Murmur3)
                .name(this.bloomRedisKey)
                .redisBacked(true)
//                .redisHost(host)
//                .redisPort(port)
                .redisConnections(10)
//                .overwriteIfExists(true)
                .pool(RedisPool.builder().host(config.getHost()).port(config.getPort()).password(config.getPassword()).build())
                .complete());
    }

    /**
     * 重置队列以实现重复抓取
     *
     * @param task
     */
    @Override
    public void resetDuplicateCheck(Task task) {
        bloomFilter.clear();// 清理掉BloomFIlter
    }

    /**
     * 判断set队列里面是否有重复的URL，没有的话，添加到set队列里面，并执行pushWhenNoDuplicate，添加到任务中
     *
     * @param request
     * @param task
     * @return
     */
    @Override
    public boolean isDuplicate(Request request, Task task) {
        boolean contains = bloomFilter.contains(request.getUrl());
        if (contains) {
            return true;
        } else {
            bloomFilter.add(request.getUrl());
        }
        return false;
    }

    /**
     * 添加任务到item队列。添加采集任务时，会先添加到queue里面，再添加到item里面。
     *
     * @param request
     * @param task
     */
    @Override
    protected void pushWhenNoDuplicate(Request request, Task task) {
        Jedis jedis = pool.getResource();
        try {
            // 如果是文章页，添加到队列前面，否则添加到后面
            if (request.getExtras() != null && request.getExtra("pagetype", PageType.PAGE_INDEX).toString().equals(PageType.PAGE_ARTICLE)) {
                jedis.lpush(getQueueKey(task), request.getUrl());
            } else if (StringUtils.isNotEmpty(regexMatchUrl) && request.getUrl().matches(regexMatchUrl)) {
//                logger.info("添加URL到Queue左侧：" + request.getUrl());
                jedis.lpush(getQueueKey(task), request.getUrl());//满足URL匹配，放到队列左侧
            } else {
//                logger.info("添加URL到Queue右侧：" + request.getUrl());
                jedis.rpush(getQueueKey(task), request.getUrl());// 放到队列尾部
            }
            if (request.getExtras() != null) {
//                String field = DigestUtils.shaHex(request.getUrl());
                String field = Md5Util.getMd516(request.getUrl());//换成16位的md5形式
                String value = JSON.toJSONString(request);
                jedis.hset((getItemKey(task)), field, value);
            }
        } finally {
//            pool.returnResource(jedis);
            close(jedis);
        }
    }

    /**
     * 从queue队列左边取出一条URL，会先从queue里面取出URL，再从item里面取出任务的明细数据出来返回
     *
     * @param task
     * @return
     */
    @Override
    public synchronized Request poll(Task task) {
        Jedis jedis = pool.getResource();
        try {
            String url = jedis.lpop(getQueueKey(task));//从左边队列弹出一条数据进行采集
            if (url == null) {
                return null;
            }
            String key = getItemKey(task);
//            String field = DigestUtils.shaHex(url);
            String field = Md5Util.getMd516(url);//换成16位的md5形式
            byte[] bytes = jedis.hget(key.getBytes(), field.getBytes());
            if (bytes != null) {
                Request o = JSON.parseObject(new String(bytes), Request.class);
                return o;
            }
            Request request = new Request(url);
            return request;
        } catch (Exception e) {
            logger.error("poll发生异常，继续执行", e);
            return null;
        } finally {
//            pool.returnResource(jedis);
            close(jedis);
        }
    }


//    protected String getSetKey(Task task) {
//        return SET_PREFIX + task.getUUID();
//    }

    protected String getQueueKey(Task task) {
        return QUEUE_PREFIX + task.getUUID();
    }

    protected String getItemKey(Task task) {
        return ITEM_PREFIX + task.getUUID();
    }

    /**
     * 获取待采集的数量，队列里面还有多少数据等待采集
     *
     * @param task
     * @return
     */
    @Override
    public int getLeftRequestsCount(Task task) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.llen(getQueueKey(task));
            return size.intValue();
        } finally {
//            pool.returnResource(jedis);
            close(jedis);
        }
    }

    /**
     * 获取总请求的结果数量，已经采集了多少数据
     *
     * @param task
     * @return
     */
    @Override
    public int getTotalRequestsCount(Task task) {
        return bloomFilter.getEstimatedPopulation().intValue();
    }

    /**
     * 关闭当前连接实例，将连接返回连接池
     *
     * @param jedis redis连接实例
     */
    private void close(Jedis jedis) {
        try {
            if (jedis != null) {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error("close jedis failed!", e);
        }
    }

}
