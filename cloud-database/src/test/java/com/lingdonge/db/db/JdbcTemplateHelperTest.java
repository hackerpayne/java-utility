package com.lingdonge.db.db;

import com.lingdonge.core.dates.DateUtil;
import com.lingdonge.core.bean.common.ModelIPLocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JdbcTemplateHelperTest {


    public static void main(String[] args) {

        // 查询一条记录
//        Map mapRow=DBSaverTemplate.getOneItem("queryList count(1) as num from user_agent limit 1");
//        System.out.println(mapRow);

        //查询记录总数
//        Integer totalCount = DBSaverTemplate.getTotalCount("user_agent");
//        System.out.println(totalCount);

        // 获取总页数
        Integer totalPage = jdbcTemplate.getTotalPage("user_agent", 1000);
        System.out.println(totalPage);

        // 分页查询数据
//        List<Map<String, Object>> listDatas = DBSaverTemplate.queryPage("user_agent", 1, 10);
//        for (Map item : listDatas) {
//            System.out.println(item);
//        }
    }


    public static JdbcTemplateHelper jdbcTemplate;


    static {
        jdbcTemplate = new JdbcTemplateHelper(
                "root", "123456", "localhost", "proxy_com", 3306, 5, 100);
    }


    private static final ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    /**
     * @param listData
     */
    public synchronized static void batchAdd(final List<String> listData) {

        JdbcTemplate template = jdbcTemplate.getTemplate();

        String sql = "insert into user_agent (ua,weight,created_at,updated_at) value(?,?,?,?) ON DUPLICATE KEY UPDATE weight= weight+1,created_at= ?,updated_at=?";
        template.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public int getBatchSize() {
                return listData.size();
            }

            @Override
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setString(1, listData.get(i));
                ps.setInt(2, 1);
                ps.setString(3, DateUtil.getNowTime());
                ps.setString(4, DateUtil.getNowTime());
                ps.setString(5, DateUtil.getNowTime());
                ps.setString(6, DateUtil.getNowTime());
            }
        });
    }

    /**
     * 更新IP地址的数据
     *
     * @param ip
     * @param localtion
     */
    public synchronized static void updateIP(String ip, ModelIPLocation localtion) {

        String curTime = sdf.get().format(new Date());

        int result = jdbcTemplate.insertOrUpdate("update ip_address set country = ? , province = ? , city = ? , isp= ?,updated_at= ?  where ip = ? ", localtion.getCountry(), localtion.getProvince(), localtion.getCity(), localtion.getIsp(), curTime, ip);

//        if (result != -1)
//            logger.info("更新IP：" + ip + "数据成功");
//        else
//            logger.info("更新IP：" + ip + "失败");


    }


    /**
     * UA插入到数据库
     *
     * @param ua
     */
    public synchronized static void insertUA(String ip, String ua) {

        String curTime = sdf.get().format(new Date());

        //第一步：插入UA
        if (StringUtils.isNotEmpty(ua) && !ua.equals("-") && ua.length() < 255) {

            ua = StringUtils.stripStart(ua, "\"");
            ua = StringUtils.stripEnd(ua, "\"");

            ua = ua.trim();

            jdbcTemplate.insertOrUpdate("insert into user_agent (ua,weight,created_at,updated_at) value(?,?,?,?) ON DUPLICATE KEY UPDATE weight= weight+1,created_at= ?,updated_at=? ", ua, "1", curTime, curTime, curTime, curTime);
        }

        // 第二步：插入IP
        if (StringUtils.isNotEmpty(ip) && ip.length() < 20 && !ip.startsWith("127.0.")) {
            ip = ip.trim();

            jdbcTemplate.insertOrUpdate("insert into ip_address (ip,created_at,updated_at) value(?,?,?) ON DUPLICATE KEY UPDATE updated_at= ? ", ip, curTime, curTime, curTime);

        }

    }

}
