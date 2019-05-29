package com.lingdonge.db.jdbc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lindonge.core.dates.DateUtil;
import com.lingdonge.db.configuration.properties.DruidProperties;
import com.lingdonge.db.util.DataSourceBuilder;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


public class JdbcTemplateUtilTest {

    private JdbcTemplateUtil jdbcTemplateUtil;

    public JdbcTemplateUtilTest() {

        DruidProperties druidProperties = new DruidProperties();
        druidProperties.setUsername("root");
        druidProperties.setPassword("123456");
        druidProperties.setDriverClassName("com.mysql.jdbc.Driver");
        druidProperties.setUrl("jdbc:mysql://123456:3377/test?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false");

        DataSource dataSource = DataSourceBuilder.createDruidDataSource(druidProperties);
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        jdbcTemplateUtil = new JdbcTemplateUtil(jdbcTemplate, "test");
    }

    @Test
    public void testInsert() {
        Map<String, Object> mapData = Maps.newHashMap();
        mapData.put("title", "test");
        System.out.println("数据插入结果：" + jdbcTemplateUtil.insert(mapData));
    }

    @Test
    public void testDelete() {
        System.out.println("数据删除结果：" + jdbcTemplateUtil.delete(1L));
    }

    @Test
    public void testBatchInsert() throws Exception {

        List<Map<String, Object>> listDatas = Lists.newArrayList();

        Map<String, Object> listItem;

        for (int i = 0; i < 100; i++) {
            listItem = Maps.newHashMap();
            listItem.put("id", i + 1);
            listItem.put("title", "hahah" + String.valueOf(i));
            listItem.put("created_at", DateUtil.getNowTime());
            listItem.put("updated_at", DateUtil.getNowTime());
            listDatas.add(listItem);
        }

//        JdbcTemplateUtil simpleDao = new JdbcTemplateUtil("1688");
//        simpleDao.setJdbcTemplate(jdbcTemplate);
//
//        simpleDao.batchInsert(listDatas);
    }

}