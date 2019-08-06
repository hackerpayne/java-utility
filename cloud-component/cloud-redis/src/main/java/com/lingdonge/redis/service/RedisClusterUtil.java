package com.lingdonge.redis.service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.*;
import redis.clients.jedis.util.JedisClusterCRC16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class RedisClusterUtil {

    private static JedisCluster jedisCluster;

    public RedisClusterUtil() {
    }

    public RedisClusterUtil(JedisCluster jedisCluster) {
        RedisClusterUtil.jedisCluster = jedisCluster;

        this.afterPropertySet();//注入和生成实例
        log.info("正在加载Redis配置文件：" + jedisCluster);
    }

    private static volatile RedisPoolUtil instance;

    /**
     * 单例模式，线程安全
     *
     * @return
     * @throws Exception
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
    }

    public List<String> getScan(Jedis redisService, String key) {
        List<String> list = new ArrayList<>();
        ScanParams params = new ScanParams();
        params.match(key);
        params.count(100);
        String cursor = "0";
        while (true) {
            ScanResult scanResult = redisService.scan(cursor, params);
            List<String> elements = scanResult.getResult();
            if (elements != null && elements.size() > 0) {
                list.addAll(elements);
            }
            cursor = scanResult.getCursor();
            if ("0".equals(cursor)) {
                break;
            }
        }
        return list;
    }

    public List<String> getRedisKeys(JedisCluster jedisCluster, String matchKey) {
        List<String> list = new ArrayList<>();
        try {
            Map<String, JedisPool> clusterNodes = jedisCluster.getClusterNodes();
            for (Map.Entry<String, JedisPool> entry : clusterNodes.entrySet()) {
                Jedis jedis = entry.getValue().getResource();
                // 判断非从节点(因为若主从复制，从节点会跟随主节点的变化而变化)
                if (!jedis.info("replication").contains("role:slave")) {
                    List<String> keys = getScan(jedis, matchKey);
                    if (keys.size() > 0) {
                        Map<Integer, List<String>> map = new HashMap<>();
                        for (String key : keys) {
                            // cluster模式执行多key操作的时候，这些key必须在同一个slot上，不然会报:JedisDataException:
                            // CROSSSLOT Keys in request don't hash to the same slot
                            int slot = JedisClusterCRC16.getSlot(key);
                            // 按slot将key分组，相同slot的key一起提交
                            if (map.containsKey(slot)) {
                                map.get(slot).add(key);
                            } else {
                                map.put(slot, Lists.newArrayList(key));
                            }
                        }
                        for (Map.Entry<Integer, List<String>> integerListEntry : map.entrySet()) {
                            // System.out.println("integerListEntry="+integerListEntry);
                            list.addAll(integerListEntry.getValue());
                        }
                    }
                }
            }
        } finally {
            return list;
        }
    }


}
