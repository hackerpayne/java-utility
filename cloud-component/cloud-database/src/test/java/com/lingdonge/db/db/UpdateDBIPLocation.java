package com.lingdonge.db.db;

import lombok.extern.slf4j.Slf4j;

/**
 * 更新数据库里面IP地址归属地信息
 * Created by kyle on 2017/6/13.
 */
@Slf4j
public class UpdateDBIPLocation {


//    public static void main(String[] args) {
//
//        String ipAddr;
//
//        Integer totalPage = DBSaverTemplate.jdbcTemplate.getTotalPage("ip_address", 100, "country is null");
//
//        logger.info(MessageFormat.format("共取得页数：{0}", totalPage));
//
//        for (Integer page = 1; page <= totalPage; page++) {
//
////            if (page < 1304) continue;//已经处理到1304页了
//
//            List<Map<String, Object>> listData = DBSaverTemplate.jdbcTemplate.queryPage("ip_address", page, 100, "country is null");
//
//            logger.info(MessageFormat.format("正在处理第【{0}/{2}】页的数据,共【{1}】条", page, listData.size(), totalPage));
//
//
//            for (Map<String, Object> item : listData) {
//
//                ipAddr = item.get("ip").toString().trim();
//
//                logger.info(ipAddr);
//
//                ModelIPLocation location = IPHelper.getIPLocation(ipAddr);
//
//                if (location == null) continue;
//
//                logger.info("IP:" + ipAddr + " location country: " + location.getCountry() + ",province:" + location.getProvince() + ",city:" + location.getCity());
//
//                //更新数据库
//                DBSaverTemplate.updateIP(ipAddr, location);
//
//
//            }
//        }
//    }


}
