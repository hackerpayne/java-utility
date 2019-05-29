package com.lingdonge.db.db.dbpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据库连接池的实现代码，创建连接和获取链接
 * Created by kyle on 2017/6/16.
 */
public class GPPoolDataSource {

    //加载配置类
    GPConfig config = new GPConfig();

    //写一个参数，用来标记当前有多少个活跃的连接
    private AtomicInteger currentActive = new AtomicInteger(0);

    //创建一个集合，干嘛的呢？用来存放连接，毕竟我们刚刚初始化的时候就需要创建initSize个连接
    //并且，当我们释放连接的时候，我们就把连接放到这里面
    Vector<Connection> freePools = new Vector<>();

    //正在使用的连接池
    Vector<GPPoolEntry> usePools = new Vector<>();

    //构造器中初始化
    public GPPoolDataSource() {
        init();
    }

    //初始化方法
    public void init() {
        try {
            //我们的jdbc是不是每次都要加载呢？肯定不是的，只要加载一次就够了
            Class.forName(config.getDriver());
            for (int i = 0; i < Integer.valueOf(config.getInitSize()); i++) {
                Connection conn = createConn();
                freePools.add(conn);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        check();
    }

    //创建连接
    public synchronized Connection createConn() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
            currentActive.incrementAndGet();
            System.out.println("创建一个连接, 当前的活跃的连接数目为：" + currentActive.get() + " 连接：" + conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 创建连接有了，是不是也应该获取连接呢？
     *
     * @return
     */
    public synchronized GPPoolEntry getConn() {
        Connection conn = null;
        if (!freePools.isEmpty()) {
            conn = freePools.get(0);
            freePools.remove(0);
        } else {
            if (currentActive.get() < Integer.valueOf(config.getMaxSize())) {
                conn = createConn();
            } else {
                try {
                    System.out.println("连接池已经满了，需要等待...");
                    wait(1000);
                    return getConn();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        GPPoolEntry poolEntry = new GPPoolEntry(conn, System.currentTimeMillis());
        //获取连接干嘛的？不就是使用的吗？所以，每获取一个，就放入正在使用池中
        usePools.add(poolEntry);
        return poolEntry;
    }


    /**
     * 创建连接，获取连接都已经有了，接下来就是该释放连接了
     */
    public synchronized void release(Connection conn) {
        try {
            if (!conn.isClosed() && conn != null) {
                freePools.add(conn);
            }
            System.out.println("回收了一个连接，当前空闲连接数为：" + freePools.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //定时检查占用时间超长的连接，并关闭
    private void check() {
        if (Boolean.valueOf(config.getHealth())) {
            Worker worker = new Worker();
            new java.util.Timer().schedule(worker, Long.valueOf(config.getDelay()), Long.valueOf(config.getPeriod()));
        }
    }

    class Worker extends TimerTask {
        @Override
        public void run() {
            System.out.println("例行检查...");
            for (int i = 0; i < usePools.size(); i++) {
                GPPoolEntry entry = usePools.get(i);
                long startTime = entry.getUseStartTime();
                long currentTime = System.currentTimeMillis();
                if ((currentTime - startTime) > Long.valueOf(config.getTimeout())) {
                    Connection conn = entry.getConn();
                    try {
                        if (conn != null && !conn.isClosed()) {
                            conn.close();
                            usePools.remove(i);
                            currentActive.decrementAndGet();
                            System.out.println("发现有超时连接，强行关闭，当前活动的连接数：" + currentActive.get());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}