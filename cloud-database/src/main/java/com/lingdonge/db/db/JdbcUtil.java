package com.lingdonge.db.db;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

/**
 * JDBC操作类
 */
public class JdbcUtil {

    public static final String DRIVER;
    public static final String URL;
    public static final String USERNAME;
    public static final String PASSWORD;

    private static Properties prop = null;

    private Connection conn = null;
    private PreparedStatement ps = null;
    protected ResultSet rs = null;

    /**
     * 加载配置文件中的信息
     */
    static {
        prop = new Properties();
        try {
            prop.load(JdbcUtil.class.getClassLoader().getResourceAsStream("database.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        DRIVER = prop.getProperty("DRIVER");
        URL = prop.getProperty("URL");
        USERNAME = prop.getProperty("USERNAME");
        PASSWORD = prop.getProperty("PASSWORD");
    }

    /**
     * 获取连接
     *
     * @return void
     * @author blindeagle
     */
    public void getConnection() {
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询的公共方法
     *
     * @param sql    执行的sql语句
     * @param params 参数列表
     * @return 结果集
     * @author blindeagle
     */
    public ResultSet getQuery(String sql, Object[] params) {
        try {
            getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * 数据的增删改操作的公共方法
     *
     * @param sql    执行的sql语句
     * @param params 参数列表
     * @return 受影响的记录条数
     * @author blineagle
     */
    public int getUpdate(String sql, Object[] params) {
        int result = 0;
        getConnection();
        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            result = ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            closeConnection();
        }
        return result;
    }

    /**
     * 关闭数据库连接
     *
     * @return void
     * @author blindeagle
     */
    public void closeConnection() {
        try {
            if (rs != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
