//package com.kyle.springutils;
//
//
//import com.kyle.utility.faker.MobileHelper;
//import com.kyle.utility.token.StringUtils;
//import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.delayqueue.JdbcTemplate;
//import org.springframework.jdbc.delayqueue.PreparedStatementSetter;
//
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.token.List;
//import java.token.Map;
//
//public class PaginationTest {
//
//    private Logger logger = LoggerFactory.getLogger(PaginationTest.class);
//
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    public void testPagination() {
//        String sql = "select * from `1688` order by id asc";
////        Pagination pagination = new Pagination(sql, 1, 100, jdbcTemplate);
////        int totalPage = pagination.getTotalPages();
////        int totalCount = pagination.getTotalRows();
//
//
//        List<Map<String, Object>> listResults;
////        String mobile;
//        Map item;
////        Integer id;
////
////        for (int page = 1; page <= totalPage; page++) {
////
////            logger.info(StringUtils.format("正在处理第【{}/{}】页数据，共[{}]条记录", page, totalPage, totalCount));
////
////            listResults = pagination.getPageData(page);
////
////            for (int j = 0; j < listResults.size(); j++) {
////                item = listResults.get(j);
////                Integer id = Integer.parseInt(item.get("id").toString());
////                String mobile = MobileHelper.clearMobile(item.get("mobile").toString());
////
////                try {
////                    //手机号为空的清理掉
////                    if (StringUtils.isEmpty(mobile)) {
////
////                        jdbcTemplate.update("delete from `1688` where id=?",
////                                new Object[]{id}, new int[]{java.sql.Types.INTEGER});
////
////                        continue;
////                    }
////
////                    MobileHelper.MobileEnum mobileEnum = MobileHelper.getMobileType(mobile);
////
////                    // 更新数据
////                    jdbcTemplate.update("update `1688` set mobile=?,mobile_type=? where id = ?",
////                            new PreparedStatementSetter() {
////                                @Override
////                                public void setValues(PreparedStatement ps) throws SQLException {
////                                    ps.setString(1, mobile);
////                                    ps.setInt(2, mobileEnum.ordinal());
////                                    ps.setInt(3, id);
////                                }
////                            }
////                    );
////                } catch (Exception ex) {
////                    logger.error(ex.getMessage());
////                }
////            }
////        }
////    }
////
////    @Test
////    public void testgetPageDataManual() {
////
////        Pagination pagination = new Pagination<>();
//////        pagination.getPageDataManual(12000, "1688", "id", "asc", 100, 28);
////        System.out.println("总记录数：" + pagination.getTotalRows());
////        System.out.println("总页数：" + pagination.getTotalPages());
////    }
//
//}