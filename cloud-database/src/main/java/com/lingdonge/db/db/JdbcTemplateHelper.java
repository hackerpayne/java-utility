package com.lingdonge.db.db;

import com.lindonge.core.file.PropertiesUtils;
import com.lindonge.core.model.ModelArticle;
import com.lindonge.core.util.StringUtils;
import com.lindonge.core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.io.FileUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;


/**
 * 使用JdbcTemplate操作数据库
 * Created by kyle on 17/4/12.
 */
@Slf4j
public class JdbcTemplateHelper {

    public static void main(String[] args) throws Exception {

        JdbcTemplateHelper jdbcTemplateHelper = JdbcTemplateHelper.getInstance();

        Integer totalCount = jdbcTemplateHelper.getTotalCount("1688");
        log.info("总数是：" + totalCount);

        List<Map<String, Object>> listDatas = jdbcTemplateHelper.queryPage("1688", "*", 1, 100);
        for (Map<String, Object> item : listDatas) {
            System.out.println(item.get("mobile"));
        }
    }


    private BasicDataSource dataSource = null;

    private JdbcTemplate jdbcTemplate;

    private static volatile JdbcTemplateHelper instance;

    /**
     * 单例模式，线程安全
     *
     * @return
     * @throws Exception
     */
    public static JdbcTemplateHelper getInstance() throws Exception {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (JdbcTemplateHelper.class) {
                //未初始化，则初始instance变量
                if (instance == null) {

                    File configFile = FileUtils.getFile(Utils.CurrentDir, "configuration", "database.properties");

                    if (!configFile.exists()) {
                        throw new Exception("配置文件:【" + configFile.getPath() + "】不存在，无法使用Redis");
                    } else {
                        PropertiesUtils propertiesUtils = new PropertiesUtils(configFile.getPath());

                        String userName = propertiesUtils.getProperty("jdbc.username", "");
                        String password = propertiesUtils.getProperty("jdbc.password", "");
                        String server = propertiesUtils.getProperty("jdbc.host", "");
                        String database = propertiesUtils.getProperty("jdbc.db", "");
                        String port = propertiesUtils.getProperty("jdbc.port", "3306");
                        String min = propertiesUtils.getProperty("jdbc.min", "0");
                        String max = propertiesUtils.getProperty("jdbc.max", "10");

                        instance = new JdbcTemplateHelper(userName, password, server, database, Integer.parseInt(port), Integer.parseInt(min), Integer.parseInt(max));

                    }


                }
            }
        }
        return instance;
    }


    /**
     * 构造函数
     *
     * @param username
     * @param password
     * @param server
     * @param dbname      数据库名称
     * @param port
     * @param initialSize
     * @param maxActive
     */
    public JdbcTemplateHelper(String username, String password, String server, String dbname, int port, int initialSize, int maxActive) {
        this.dataSource = new BasicDataSource();
        this.dataSource.setDriverClassName("com.mysql.jdbc.Driver");

        String url = MessageFormat.format("jdbc:mysql://{0}:{2}/{1}?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false", server, dbname, Integer.toString(port));

        System.out.println("链接字符串：" + url);

        this.dataSource.setUrl(url);
        this.dataSource.setUsername(username);
        this.dataSource.setPassword(password);
        this.dataSource.setInitialSize(initialSize);
        this.dataSource.setMaxActive(maxActive);
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    /**
     * 传递一个JDBCTemplate进来进行管理
     *
     * @param template
     */
    public JdbcTemplateHelper(JdbcTemplate template) {
        if (template != null) {
            setTemplate(template);
        }
    }

    /**
     * @return
     */
    public BasicDataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * @param dataSource
     */
    public void setDataSource(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return
     */
    public JdbcTemplate getTemplate() {
        return this.jdbcTemplate;
    }

    /**
     * @param template
     */
    public void setTemplate(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    /**
     * 关闭数据源
     *
     * @throws SQLException
     */
    public void close() throws SQLException {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    /**
     * 执行SQL，可用于新建或者删除等操作时使用
     * 使用Jdbc创建数据库,SQL示例：
     * "CREATE TABLE IF NOT EXISTS tb_content ("
     * + "id int(11) NOT NULL AUTO_INCREMENT,"
     * + "title varchar(50),url varchar(200),html longtext,"
     * + "PRIMARY KEY (id)"
     * + ") ENGINE=MyISAM DEFAULT CHARSET=utf8;"
     *
     * @param sql
     */
    public boolean executeSQL(String sql) {
        try {
            jdbcTemplate.execute(sql);//执行SQL
            return true;
        } catch (Exception ex) {
            log.error("执行SQL:" + sql, ex);
            return false;
        }
    }

    /**
     * 获取总表记录数
     *
     * @param table
     * @param pageSize
     * @return
     */
    public Integer getTotalPage(String table, Integer pageSize) {
        return getTotalPage(table, pageSize, "");
    }

    /**
     * 获取总页数
     *
     * @param table
     * @param pageSize
     * @param whereStr 条件语句
     * @return
     */
    public Integer getTotalPage(String table, Integer pageSize, String whereStr) {

        try {
            Integer totalCount = getTotalCount(table, whereStr);

            return (totalCount + pageSize - 1) / pageSize;
        } catch (Exception ex) {
            log.error("getTotalPage异常", ex);
            return -1;
        }

    }

    /**
     * 获取总数
     *
     * @param table
     * @return
     */
    public Integer getTotalCount(String table) {
        return getTotalCount(table, "");
    }

    /**
     * 获取总数
     *
     * @param table
     * @param where 条件语句
     * @return
     */
    public Integer getTotalCount(String table, String where) {

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(where)) {
            where = " where " + where;
        } else {
            where = "";
        }

        String sql = StringUtils.format("select count(1) as num from `{}` {}", table, where);

        log.info(sql);

        // 将数据插入mysql
        if (jdbcTemplate != null) {
            Map mapRow = jdbcTemplate.queryForMap(sql.trim());
            return Integer.parseInt(mapRow.get("num").toString());
        }
        return -1;
    }

    /**
     * 获取一条数据
     *
     * @param sql
     * @return
     */
    public Map queryOne(String sql) {

        // 将数据插入mysql
        if (jdbcTemplate != null) {
            return jdbcTemplate.queryForMap(sql);
        }
        return null;
    }

    /**
     * 获取SQL查询的结果
     *
     * @param sql
     * @return
     */
    public List<Map<String, Object>> queryList(String sql) {
        try {

            // 将数据插入mysql
            if (jdbcTemplate != null) {
                return jdbcTemplate.queryForList(sql);
            }
        } catch (Exception e) {
            log.error("queryPage发生异常", e);
        }
        return null;
    }

    /**
     * 分页查询数据
     *
     * @param table
     * @param page
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> queryPage(String table, Integer page, Integer pageSize) {
        return queryPage(table, "", "", page, pageSize, "", "");
    }

    /**
     * 分页查询数据
     *
     * @param table
     * @param page
     * @param pageSize
     * @param whereStr
     * @return
     */
    public List<Map<String, Object>> queryPage(String table, Integer page, Integer pageSize, String whereStr) {
        return queryPage(table, "", "", page, pageSize, "", whereStr);
    }

    /**
     * 分页查询数据
     *
     * @param table
     * @param page
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> queryPage(String table, String column, Integer page, Integer pageSize) {
        return queryPage(table, column, "", page, pageSize, "", "");
    }

    /**
     * 分页查询数据
     *
     * @param table
     * @param page
     * @param pageSize
     * @param whereStr
     * @return
     */
    public List<Map<String, Object>> queryPage(String table, String column, Integer page, Integer pageSize, String whereStr) {
        return queryPage(table, column, "", page, pageSize, "", whereStr);
    }

    /**
     * 分页查询所有信息
     *
     * @param table    查询的表
     * @param mainKey  排序键
     * @param page     页码
     * @param pageSize 每页大小
     * @param sort     排序方式
     * @return
     */
    public List<Map<String, Object>> queryPage(String table, String columns, String mainKey, Integer page, Integer pageSize, String sort, String whereStr) {

        if (page < 1) {
            page = 1;
        }

        if (StringUtils.isEmpty(mainKey)) {
            mainKey = "id";
        }
        if (StringUtils.isEmpty(sort)) {
            sort = "desc";
        }

        //判断是否有where条件
        if (StringUtils.isNotEmpty(whereStr)) {
            whereStr = " where " + whereStr;
        } else {
            whereStr = "";
        }

        String columnStr = "";
        if (StringUtils.isEmpty(columns)) {
            columnStr = "*";
        } else {
            columnStr = columns;
        }

        String sql = MessageFormat.format("select {6} from `{0}` {5} order by {3} {4} limit {1,number,#},{2,number,#}", table, (page - 1) * pageSize, pageSize, mainKey, sort, whereStr, columnStr);

        return queryList(sql);
    }

    /**
     * 插入或者更新语句，返回受影响的行数
     * 适合于insert 、update和delete操作
     *
     * @param sql  执行插入的SQL语句
     * @param args
     * @return
     */
    public synchronized int insertOrUpdate(String sql, Object... args) {

//        log.info("SQL:"+sql);
//        log.info("Param:"+ Joiner.on("----").join(args));

        // 将数据插入mysql
        if (jdbcTemplate != null) {
            return jdbcTemplate.update(sql, args);
        }
        return -1;
    }
//
//    /**
//     * 第一个参数为执行sql
//     * 第二个参数为参数数据
//     * 第三个参数为参数类型
//     */
//    public void save(ModelUrl user) {
//        jdbcTemplate.update(
//                "insert into tb_test1(name,password) values(?,?)",
//                new Object[]{user.getCleanUrl(), user.getCleanUrl()},
//                new int[]{java.sql.Types.VARCHAR, java.sql.Types.VARCHAR}
//        );
//    }
//
//    //避免sql注入
//    public void save2(final ModelUrl user) {
//
//        jdbcTemplate.update("insert into tb_test1(name,password) values(?,?)",
//                new PreparedStatementSetter() {
//
//                    @Override
//                    public void setValues(PreparedStatement ps) throws SQLException {
//                        ps.setString(1, user.getCleanUrl());
//                        ps.setString(2, user.getFragment());
//                    }
//                });
//
//    }
//
//
//    //返回插入的主键
//    public List save5(final ModelUrl user) {
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//
//        jdbcTemplate.update(new PreparedStatementCreator() {
//
//                                @Override
//                                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//                                    PreparedStatement ps = connection.prepareStatement("insert into tb_test1(name,password) values(?,?)", new String[]{"id"});
//                                    ps.setString(1, user.getFragment());
//                                    ps.setString(2, user.getCleanUrl());
//                                    return ps;
//                                }
//                            },
//                keyHolder);
//
//        return keyHolder.getKeyList();
//    }
//
//    public void update(final ModelUrl user) {
//        jdbcTemplate.update(
//                "update tb_test1 set name= ? ,password= ? where id = ?",
//                new PreparedStatementSetter() {
//                    @Override
//                    public void setValues(PreparedStatement ps) throws SQLException {
//                        ps.setString(1, user.getFragment());
//                        ps.setString(2, user.getCleanUrl());
//                        ps.setString(3, user.getFragment());
//                    }
//                }
//        );
//    }
//
//    public void delete(ModelArticle user) {
//        jdbcTemplate.update(
//                "delete from tb_test1 where id = ?",
//                new Object[]{user.getArtid()},
//                new int[]{java.sql.Types.INTEGER});
//    }
//
//
//    //可以返回是一个基本类型的值
//    public String queryForObject1(ModelUrl user) {
//        return (String) jdbcTemplate.queryForObject("select username from tb_test1 where id = 100",
//                String.class);
//    }
//
//    //可以返回值是一个对象
//    public ModelUrl queryForObject2(ModelUrl user) {
//        return (ModelUrl) jdbcTemplate.queryForObject("select * from tb_test1 where id = 100", ModelUrl.class); //class是结果数据的java类型
//    }
//
//    @Deprecated //因为没有查询条件，所以用处不大
//    public ModelArticle queryForObject3(ModelArticle user) {
//        return (ModelArticle) jdbcTemplate.queryForObject("select * from tb_test1 where id = 100",
//                new RowMapper() {
//
//                    @Override
//                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        ModelArticle user = new ModelArticle();
//                        user.setArtid(rs.getInt("id"));
//                        user.setTitle(rs.getString("username"));
//                        user.setBody(rs.getString("password"));
//                        return user;
//                    }
//                }
//        );
//    }
//
//    public ModelArticle queryForObject4(ModelArticle user) {
//        return (ModelArticle) jdbcTemplate.queryForObject("select * from tb_test1 where id = ?",
//                new Object[]{user.getArtid()},
//                ModelArticle.class); //class是结果数据的java类型  实际上这里是做反射，将查询的结果和User进行对应复制
//    }
//
//    public ModelArticle queryForObject5(ModelArticle user) {
//        return (ModelArticle) jdbcTemplate.queryForObject(
//                "select * from tb_test1 where id = ?",
//                new Object[]{user.getArtid()},
//                new RowMapper() {
//
//                    @Override
//                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        ModelArticle user = new ModelArticle();
//                        user.setArtid(rs.getInt("id"));
//                        user.setTitle(rs.getString("username"));
//                        user.setBody(rs.getString("password"));
//                        return user;
//                    }
//
//                }); //class是结果数据的java类型
//    }
//
//    public ModelArticle queryForObject(ModelArticle user) {
//        //方法有返回值
//        return (ModelArticle) jdbcTemplate.queryForObject("select * from tb_test1 where id = ?",
//                new Object[]{user.getArtid()},
//                new int[]{java.sql.Types.INTEGER},
//                new RowMapper() {
//
//                    @Override
//                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        ModelArticle user = new ModelArticle();
//                        user.setArtid(rs.getInt("id"));
//                        user.setTitle(rs.getString("username"));
//                        user.setBody(rs.getString("password"));
//                        return user;
//                    }
//                }
//        );
//    }
//
//    public List<ModelArticle> queryForList1(ModelArticle user) {
//        return (List<ModelArticle>) jdbcTemplate.queryForList("select * from tb_test1 where username = ?",
//                new Object[]{user.getArtid()},
//                ModelArticle.class);
//    }
//
//    public List<String> queryForList2(ModelArticle user) {
//        return (List<String>) jdbcTemplate.queryForList("select username from tb_test1 where sex = ?",
//                new Object[]{user.getArtid()},
//                String.class);
//    }
//
//    @SuppressWarnings("unchecked")
//    //最全的参数查询
//    public List<ModelArticle> queryForList3(ModelArticle user) {
//        return (List<ModelArticle>) jdbcTemplate.queryForList("select * from tb_test1 where username = ?",
//                new Object[]{user.getArtid()},
//                new int[]{java.sql.Types.VARCHAR},
//                ModelArticle.class);
//    }
//
//    //通过RowCallbackHandler对Select语句得到的每行记录进行解析，并为其创建一个User数据对象。实现了手动的OR映射。
//    public ModelArticle queryUserById4(String id) {
//        final ModelArticle user = new ModelArticle();
//
//        //该方法返回值为void
//        this.jdbcTemplate.query("select * from tb_test1 where id = ?",
//                new Object[]{id},
//                new RowCallbackHandler() {
//
//                    @Override
//                    public void processRow(ResultSet rs) throws SQLException {
//                        ModelArticle user = new ModelArticle();
//                        user.setArtid(rs.getInt("id"));
//                        user.setTitle(rs.getString("username"));
//                        user.setBody(rs.getString("password"));
//                    }
//                });
//
//        return user;
//    }
//
//    public List<ModelArticle> list(ModelArticle user) {
//        return jdbcTemplate.query("select * from tb_test1 where username like '%?%'",
//                new Object[]{user.getArtid()},
//                new int[]{java.sql.Types.VARCHAR},
//                new RowMapper() {
//
//                    @Override
//                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        ModelArticle user = new ModelArticle();
//                        user.setArtid(rs.getInt("id"));
//                        user.setTitle(rs.getString("username"));
//                        user.setBody(rs.getString("password"));
//                        return user;
//                    }
//                });
//    }

    //批量操作    适合于增、删、改操作
    public int[] batchUpdate(final List users) {

        int[] updateCounts = jdbcTemplate.batchUpdate(
                "update tb_test1 set username = ?, password = ? where id = ?",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, ((ModelArticle) users.get(i)).getTitle());
                        ps.setString(2, ((ModelArticle) users.get(i)).getBody());
                        ps.setLong(3, ((ModelArticle) users.get(i)).getArtid());
                    }

                    @Override
                    public int getBatchSize() {
                        return users.size();
                    }
                }
        );

        return updateCounts;
    }

    /**
     * 调用存储过程
     *
     * @param procedureName
     * @param args
     */
    public void callProcedure(String procedureName, Object... args) {
        this.jdbcTemplate.update("call " + procedureName, args);
    }

}
