package com.lingdonge.redis.service;


import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.concurrent.ExecutionException;

public class LettuceUtil {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RedisURI redisURI1 = RedisURI.builder().withHost("127.0.0.1").withPort(6379).withPassword("00000000").build();
        RedisURI redisURI2 = RedisURI.Builder.redis("127.0.0.1").withPort(6379).withPassword("00000000").build();
//        RedisURI redisURI3 = new RedisURI("127.0.0.1", 6379,6000L, Duration.ofMillis(-1));
        RedisClient redisClient = RedisClient.create(redisURI1);

        StatefulRedisConnection connection = redisClient.connect();
        RedisCommands<String, String> commands = connection.sync();
        System.out.println(commands.get("name"));

        RedisAsyncCommands<String, String> asyncCommands = connection.async();
        RedisFuture<String> rs = asyncCommands.get("wk");
        rs.thenAccept(System.out::println);
        while (!rs.isDone()) {
            System.out.println(rs.get());
        }

        connection.close();
        redisClient.shutdown();

    }
}
