package com.lingdonge.db.db;


import java.sql.*;

public class JdbcSimple {

    private static final String URL = "jdbc:mysql://localhost:3306/spring_test_com?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false";
    private static final String NAME = "root";
    private static final String PASSWORD = "123456";


    public static void TestAdd() throws SQLException {
        //添加女神
        Connection con = DriverManager.getConnection(URL, NAME, PASSWORD);//首先拿到数据库的连接
        String sql = "" +
                "insert into imooc_goddess" +
                "(user_name,sex,age,birthday,email,mobile," +
                "create_user,create_date,update_user,update_date,isdel) " +
                "values(" +
                "?,?,?,?,?,?,?,current_date(),?,current_date(),?)";//参数用?表示，相当于占位符;用mysql的日期函数current_date()来获取当前日期
        //预编译sql语句
        PreparedStatement psmt = con.prepareStatement(sql);
        //先对应SQL语句，给SQL语句传递参数
        psmt.setString(1, "username");
        psmt.setInt(2, 2);
        //注意：setDate()函数第二个参数需要的是java.sql.Date类型，我们传进来的是java.token.Date，类型不符，需要做一下转换
//            psmt.setDate(4, new java.token.Date().getTime());

        //执行SQL语句
        psmt.execute();
        /**
         * prepareStatement这个方法会将SQL语句加载到驱动程序conn集成程序中，但是并不直接执行
         * 而是当它调用execute()方法的时候才真正执行；
         *
         * 上面SQL中的参数用?表示，相当于占位符，然后在对参数进行赋值。
         * 当真正执行时，这些参数会加载在SQL语句中，把SQL语句拼接完整才去执行。
         * 这样就会减少对数据库的操作
         */

    }

    public static void main(String[] args) throws Exception {

        //1.加载驱动程序
        Class.forName("com.mysql.jdbc.Driver");
        //2.获得数据库的连接
        Connection conn = DriverManager.getConnection(URL, NAME, PASSWORD);
        //3.通过数据库的连接操作数据库，实现增删改查
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from user");//选择import java.sql.ResultSet;
        while (rs.next()) {//如果对象中有数据，就会循环打印出来
            System.out.println(rs.getString("username") + "," + rs.getString("password"));
        }

        rs.close();
        stmt.close();

        conn.close();
    }
}