package com.lingdonge.db.db.dbpool;

import java.sql.Connection;

/**
 * Created by kyle on 2017/6/16.
 */
public class GPPoolEntry {

    private Connection conn;
    private long useStartTime;

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public long getUseStartTime() {
        return useStartTime;
    }

    public void setUseStartTime(long useStartTime) {
        this.useStartTime = useStartTime;
    }

    public GPPoolEntry(Connection conn, long useStartTime) {
        super();
        this.conn = conn;
        this.useStartTime = useStartTime;
    }
}