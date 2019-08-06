package com.lingdonge.redis.ratelimit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的限流器：
 * 参考代码：
 * https://github.com/tangaiyun/RedisRateLimiter
 * https://github.com/tangaiyun/redislimiter-spring-boot
 */
public class RedisRateLimiter {

    private JedisPool jedisPool;
    private TimeUnit timeUnit;
    private int permitsPerUnit;

    private static final String LUA_SECOND_SCRIPT = " local current; "
            + " current = redis.call('incr',KEYS[1]); "
            + " if tonumber(current) == 1 then "
            + " 	redis.call('expire',KEYS[1],ARGV[1]); "
            + "     return 1; "
            + " else"
            + " 	if tonumber(current) <= tonumber(ARGV[2]) then "
            + "     	return 1; "
            + "		else "
            + "			return -1; "
            + "     end "
            + " end ";
    private static final String LUA_PERIOD_SCRIPT = " local currentSectionCount;"
            + " local previosSectionCount;"
            + " local totalCountInPeriod;"
            + " currentSectionCount = redis.call('zcount', KEYS[2], '-inf', '+inf');"
            + " previosSectionCount = redis.call('zcount', KEYS[1], ARGV[3], '+inf');"
            + " totalCountInPeriod = tonumber(currentSectionCount)+tonumber(previosSectionCount);"
            + " if totalCountInPeriod < tonumber(ARGV[5]) then "
            + " 	redis.call('zadd',KEYS[2],ARGV[1],ARGV[2]);"
            + "		if tonumber(currentSectionCount) == 0 then "
            + "			redis.call('expire',KEYS[2],ARGV[4]); "
            + "		end "
            + "     return 1"
            + "	else "
            + " 	return -1"
            + " end ";

    private static final int PERIOD_SECOND_TTL = 10;
    private static final int PERIOD_MINUTE_TTL = 2 * 60 + 10;
    private static final int PERIOD_HOUR_TTL = 2 * 3600 + 10;
    private static final int PERIOD_DAY_TTL = 2 * 3600 * 24 + 10;

    private static final int MICROSECONDS_IN_MINUTE = 60 * 1000000;
    private static final int MICROSECONDS_IN_HOUR = 3600 * 1000000;
    private static final int MICROSECONDS_IN_DAY = 24 * 3600 * 1000000;

    /**
     * @param jedisPool
     * @param timeUnit       控制单位，比如分钟或者小时
     * @param permitsPerUnit 限定次数
     */
    public RedisRateLimiter(JedisPool jedisPool, TimeUnit timeUnit, int permitsPerUnit) {
        this.jedisPool = jedisPool;
        this.timeUnit = timeUnit;
        this.permitsPerUnit = permitsPerUnit;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getPermitsPerSecond() {
        return permitsPerUnit;
    }

    /**
     * 判断是否可以通过
     *
     * @param keyPrefix
     * @return
     */
    public boolean acquire(String keyPrefix) {
        boolean rtv = false;
        if (jedisPool != null) {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                if (timeUnit == TimeUnit.SECONDS) {
                    String keyName = getKeyNameForSecond(jedis, keyPrefix);

                    List<String> keys = new ArrayList<String>();
                    keys.add(keyName);
                    List<String> argvs = new ArrayList<String>();
                    argvs.add(String.valueOf(getExpire()));
                    argvs.add(String.valueOf(permitsPerUnit));
                    Long val = (Long) jedis.eval(LUA_SECOND_SCRIPT, keys, argvs);
                    rtv = (val > 0);

                } else if (timeUnit == TimeUnit.MINUTES || timeUnit == TimeUnit.HOURS || timeUnit == TimeUnit.DAYS) {
                    rtv = doPeriod(jedis, keyPrefix);
                }
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return rtv;
    }

    private boolean doPeriod(Jedis jedis, String keyPrefix) {
        String[] keyNames = getKeyNames(jedis, keyPrefix);
        long currentTimeInMicroSecond = getRedisTime(jedis);
        String previousSectionBeginScore = String.valueOf((currentTimeInMicroSecond - getPeriodMicrosecond()));
        String expires = String.valueOf(getExpire());
        String currentTimeInMicroSecondStr = String.valueOf(currentTimeInMicroSecond);
        List<String> keys = new ArrayList<String>();
        keys.add(keyNames[0]);
        keys.add(keyNames[1]);
        List<String> argvs = new ArrayList<String>();
        argvs.add(currentTimeInMicroSecondStr);
        argvs.add(currentTimeInMicroSecondStr);
        argvs.add(previousSectionBeginScore);
        argvs.add(expires);
        argvs.add(String.valueOf(permitsPerUnit));
        Long val = (Long) jedis.eval(LUA_PERIOD_SCRIPT, keys, argvs);
        return (val > 0);
    }

    /**
     * 因为redis访问实际上是单线程的，而且jedis.time()方法返回的时间精度为微秒级，每一个jedis.time()调用耗时应该会超过1微秒，因此我们可以认为每次jedis.time()返回的时间都是唯一且递增的
     */
    private long getRedisTime(Jedis jedis) {
        List<String> jedisTime = jedis.time();
        Long currentSecond = Long.parseLong(jedisTime.get(0));
        Long microSecondsElapseInCurrentSecond = Long.parseLong(jedisTime.get(1));
        Long currentTimeInMicroSecond = currentSecond * 1000000 + microSecondsElapseInCurrentSecond;
        return currentTimeInMicroSecond;
    }

    private String getKeyNameForSecond(Jedis jedis, String keyPrefix) {
        String keyName = keyPrefix + ":" + jedis.time().get(0);
        return keyName;
    }

    private String[] getKeyNames(Jedis jedis, String keyPrefix) {
        String[] keyNames = null;
        if (timeUnit == TimeUnit.MINUTES) {
            long index = Long.parseLong(jedis.time().get(0)) / 60;
            String keyName1 = keyPrefix + ":" + (index - 1);
            String keyName2 = keyPrefix + ":" + index;
            keyNames = new String[]{keyName1, keyName2};
        } else if (timeUnit == TimeUnit.HOURS) {
            long index = Long.parseLong(jedis.time().get(0)) / 3600;
            String keyName1 = keyPrefix + ":" + (index - 1);
            String keyName2 = keyPrefix + ":" + index;
            keyNames = new String[]{keyName1, keyName2};
        } else if (timeUnit == TimeUnit.DAYS) {
            long index = Long.parseLong(jedis.time().get(0)) / (3600 * 24);
            String keyName1 = keyPrefix + ":" + (index - 1);
            String keyName2 = keyPrefix + ":" + index;
            keyNames = new String[]{keyName1, keyName2};
        } else {
            throw new java.lang.IllegalArgumentException("Don't support this TimeUnit: " + timeUnit);
        }
        return keyNames;
    }

    private int getExpire() {
        int expire = 0;
        if (timeUnit == TimeUnit.SECONDS) {
            expire = PERIOD_SECOND_TTL;
        } else if (timeUnit == TimeUnit.MINUTES) {
            expire = PERIOD_MINUTE_TTL;
        } else if (timeUnit == TimeUnit.HOURS) {
            expire = PERIOD_HOUR_TTL;
        } else if (timeUnit == TimeUnit.DAYS) {
            expire = PERIOD_DAY_TTL;
        } else {
            throw new java.lang.IllegalArgumentException("Don't support this TimeUnit: " + timeUnit);
        }
        return expire;
    }

    private int getPeriodMicrosecond() {
        if (timeUnit == TimeUnit.MINUTES) {
            return MICROSECONDS_IN_MINUTE;
        } else if (timeUnit == TimeUnit.HOURS) {
            return MICROSECONDS_IN_HOUR;
        } else if (timeUnit == TimeUnit.DAYS) {
            return MICROSECONDS_IN_DAY;
        } else {
            throw new java.lang.IllegalArgumentException("Don't support this TimeUnit: " + timeUnit);
        }
    }

}
