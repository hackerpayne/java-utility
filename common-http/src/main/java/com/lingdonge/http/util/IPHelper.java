package com.lingdonge.http.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lingdonge.core.http.HttpHelper;
import com.lingdonge.core.bean.common.ModelIPLocation;
import com.lingdonge.core.thirdparty.qqwry.QQwryUtils;
import com.lingdonge.core.util.StringUtils;
import com.lingdonge.http.thirdparty.MaxMindGeoHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * IP地址辅助判断类
 * 优先使用百度API、不成功使用高德API,不成功使用淘宝API,不成功使用新浪API
 * Created by Kyle on 16/8/22.
 */
@Slf4j
public class IPHelper {

    /**
     * 百度地图API Token
     */
    private static String baiduToken;

    /**
     * 高德地图API Token
     */
    private static String gaodeToken;

    /**
     * Http辅助类
     */
    final private static HttpHelper http = new HttpHelper();

    /**
     * 构造函数
     */
    public IPHelper() {
        this.baiduToken = "0IiGLYMKLTUs7ZfAHN1H4IqNVTkDiDWS";
        this.gaodeToken = "a6ffde6ed665cd5968ce70268bad3150";
    }

    public static void main(String[] args) {
        System.out.println("multithreading ipHelper");

//        for (int i=0;i<1000;i++)
//            System.out.println(getFakeIp());

//
//        String data="{\"code\":0,\"data\":{\"country\":\"\\u4e2d\\u56fd\",\"country_id\":\"CN\",\"area\":\"\\u534e\\u5317\",\"area_id\":\"100000\",\"region\":\"\\u5317\\u4eac\\u5e02\",\"region_id\":\"110000\",\"city\":\"\\u5317\\u4eac\\u5e02\",\"city_id\":\"110100\",\"county\":\"\\u6d77\\u6dc0\\u533a\",\"county_id\":\"110108\",\"isp\":\"\\u8054\\u901a\",\"isp_id\":\"100026\",\"ip\":\"221.221.153.118\"}}";
//
//        JSONObject jsonObj=JSON.parseObject(data);
//        System.out.println(jsonObj.get("data"));
//
        String ipAddr = "210.12.126.10";
        ModelIPLocation location = IPHelper.getIPLocation(ipAddr);

        System.out.println("IP:" + ipAddr + " location country: " + location.getCountry() + ",province:" + location.getProvince() + ",city:" + location.getCity());
    }

    /**
     * 生成随机IP
     *
     * @return
     */
    public static String getFakeIp() {
        String sec1 = String.valueOf(RandomUtils.nextInt(1, 254));
        String sec2 = String.valueOf(RandomUtils.nextInt(1, 254));
        String sec3 = String.valueOf(RandomUtils.nextInt(1, 254));
        String sec4 = String.valueOf(RandomUtils.nextInt(1, 254));
        return sec1 + "." + sec2 + "." + sec3 + "." + sec4;
    }

    public static final MaxMindGeoHelper maxMind = new MaxMindGeoHelper();

    /**
     * 获取IP地址定位信息,会根据IP进行多次判断
     *
     * @param ipAddr IP地址
     */
    public static ModelIPLocation getIPLocation(String ipAddr) {

        ModelIPLocation location = getIPLocationByTaobao(ipAddr);

        if (location == null) {
            log.info("getIPLocationByTaobao获取IP信出错，改用Baidu进行获取");
            location = getIPLocationByBaidu(ipAddr);
        }

        if (location == null || org.apache.commons.lang3.StringUtils.isEmpty(location.getCountry())) {
            log.info("getIPLocationByBaidu获取IP信出错，改用getIPLocationByGaode高德进行获取");
            location = getIPLocationByGaode(ipAddr);
        }

        if (location == null || org.apache.commons.lang3.StringUtils.isEmpty(location.getCountry())) {
            log.info("getIPLocationByGaode获取IP信出错，改用MaxMind的GeoIP进行获取");
            location = maxMind.parseIP(ipAddr);
        }

        if (location == null || org.apache.commons.lang3.StringUtils.isEmpty(location.getCountry())) {
            log.info("MaxMindGeoHelper获取IP信出错，改用QQWry进行获取");
            location = QQwryUtils.getIPLocation(ipAddr);
        }

        return location;
    }

    /**
     * 获取指定城市下面的任何一个IP地址，用于模拟假的IP使用
     *
     * @param city
     * @return
     */
    public static String getIpFromLocation(String city) {
        String fakeIp = "";
        return fakeIp;
    }

    /**
     * 使用淘宝IP库查询IP地址归属地
     *
     * @param ipAddr
     * @return
     */
    public static ModelIPLocation getIPLocationByTaobao(String ipAddr) {

        ModelIPLocation location = null;

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(ipAddr)) {

            ipAddr = ipAddr.trim();

            String html = null;

            // 使用淘宝库
            try {
                String queryUrl = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ipAddr.trim();

                html = http.getHtml(queryUrl);

                JSONObject obj = JSON.parseObject(html);

//                log.info(html);

                JSONObject dataObj = obj.getJSONObject("data");

                String country = dataObj.getString("country");
                String province = dataObj.getString("region");
                String city = dataObj.getString("city");
                String isp = dataObj.getString("isp");

                if (StringUtils.isAllNotEmpty(country, province, city, isp)) {

                    location = new ModelIPLocation(ipAddr);
                    location.setCountry(country);
                    location.setProvince(province);
                    location.setCity(city);
                    location.setIsp(isp);

                }

            } catch (Exception e) {
                log.error("IP:[" + ipAddr.trim() + "],使用淘宝API查询出错，HTML：" + html, e);
                location = null;
            }
        }

        return location;
    }

    /**
     * 使用百度地图IP库，查询IP地址归属地
     *
     * @param ipAddr
     * @return
     */
    public static ModelIPLocation getIPLocationByBaidu(String ipAddr) {
        ModelIPLocation location = null;

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(ipAddr)) {

            ipAddr = ipAddr.trim();

            String html = null;

            //使用百度库
            try {
                String queryUrl = "http://api.map.baidu.com/location/ip?ak=" + baiduToken + "&ip=" + ipAddr.trim() + "&coor=bd09ll";

                html = http.getHtml(queryUrl);

                JSONObject obj = JSON.parseObject(html);

//                log.info(html);

                JSONObject dataObj = obj.getJSONObject("content").getJSONObject("address_detail");

//                String country = dataObj.getString("country");
                String province = dataObj.getString("province");
                String city = dataObj.getString("city");
//                String isp = dataObj.getString("isp");

                if (StringUtils.isAllNotEmpty(province, city)) {

                    location = new ModelIPLocation(ipAddr);
                    location.setProvince(province);
                    location.setCity(city);
                }

            } catch (Exception e) {
                log.error("IP:[" + ipAddr.trim() + "],使用淘宝API查询出错,HTML:" + html, e);
                location = null;
            }

        }
        return location;

    }

    /**
     * 使用高德地图的API查询IP地址归属地
     *
     * @param ipAddr
     * @return
     */
    public static ModelIPLocation getIPLocationByGaode(String ipAddr) {
        ModelIPLocation location = null;

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(ipAddr)) {

            ipAddr = ipAddr.trim();

            String html = null;

            //使用高德库
            try {
                String queryUrl = "http://restapi.amap.com/v3/ip?ip=" + ipAddr.trim() + "&output=json&key=" + gaodeToken;

                html = http.getHtml(queryUrl);

                JSONObject obj = JSON.parseObject(html);

//                String country = obj.getString("country");
                String province = obj.getString("province");
                String city = obj.getString("city");
//                String isp = obj.getString("isp");

                if (StringUtils.isAllNotEmpty(province, city)) {

                    location = new ModelIPLocation(ipAddr);
//                    location.setCountry(country);
                    location.setProvince(province);
                    location.setCity(city);
//                    location.setIsp(isp);
                }

            } catch (Exception e) {
                log.error("IP:[" + ipAddr.trim() + "],使用高德查询出错，HTML：" + html, e);
                location = null;

            }

        }
        return location;
    }


    /**
     * 获取请求IP
     *
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-real-ip");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
            if (ip != null) {
                ip = ip.split(",")[0].trim();
            }
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

}
