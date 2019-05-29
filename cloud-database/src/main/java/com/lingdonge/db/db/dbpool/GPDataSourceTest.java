package com.lingdonge.db.db.dbpool;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 测试数据库连接池的代码
 * 没错，数据库连接池就是为数据库连接建立一个“缓冲池”，预先在“缓冲池”中放入一定数量的连接欸，当需要建立数据库连接时，从“缓冲池”中取出一个，使用完毕后再放进去。这样的好处是，可以避免频繁的进行数据库连接占用很多的系统资源。
 *
 * #文件名：database.properties
 jdbc.driver=com.mysql.jdbc.Driver
 jdbc.url=jdbc:mysql://localhost:3306/ssm
 jdbc.username=root
 jdbc.password=lfdy
 jdbc.initSize=3
 jdbc.maxSize=10
 #是否启动检查
 jdbc.health=true
 #检查延迟时间
 jdbc.delay=3000
 #间隔时间
 jdbc.period=3000
 jdbc.timeout=100000
 * Created by kyle on 2017/6/16.
 */
public class GPDataSourceTest {

    public static void main(String[] args) {

        final GPPoolDataSource dataSource = new GPPoolDataSource();

//        Runnable runnable = () -> {
//            Connection conn = dataSource.getConn().getConn();
//            System.out.println(conn);
//        };

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                Connection conn = dataSource.getConn().getConn();
                System.out.println(conn);
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 60; i++) {
            executorService.submit(runnable1);
        }
        executorService.shutdown();
    }

}