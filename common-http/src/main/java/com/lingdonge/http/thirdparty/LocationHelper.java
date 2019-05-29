package com.lingdonge.http.thirdparty;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kyle on 17/5/4.
 */
@Slf4j
public class LocationHelper {

    /**
     * 采集国家：http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201703/t20170310_1471429.html 省市区代码列表
     *
     * @throws IOException
     */
    public static void getLocations() throws IOException {
        Document doc = Jsoup.connect("http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201703/t20170310_1471429.html").get();
        Element masthead = doc.select("div.xilan_con").first().
                getElementsByClass("TRS_Editor").first().
                getElementsByClass("TRS_PreAppend").first();
        Elements allElements = masthead.getElementsByTag("p");

        List<Location> provinceList = new ArrayList<Location>();
        Location province = null;
        Location city = null;
        for (Element element : allElements) {
            String html = element.select("span[lang]").first().html();
            String locationCode = getLocationCode(html);
            String locationName = element.select("span[style]").last().html();
            if (locationCode.endsWith("0000")) {    //省或直辖市
                province = new Location(locationCode, locationName);
                province.setChildren(new ArrayList<Location>());
                provinceList.add(province);
            } else if (locationCode.endsWith("00")) {    //市
                city = new Location(locationCode, locationName);
                city.setChildren(new ArrayList<Location>());
                province.getChildren().add(city);
            } else {    //县或区
                Location county = new Location(locationCode, locationName);
                city.getChildren().add(county);
            }
        }

        Location root = new Location("0", "root");
        root.setChildren(provinceList);
        String jsonObj = JSON.toJSONString(root);
        System.out.println(jsonObj.toString());
    }

    public static String getLocationCode(String html) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        return m.replaceAll("").trim();
    }

    public static class Location {
        String code;
        String name;
        List<Location> children;

        public Location() {

        }

        public Location(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Location> getChildren() {
            return children;
        }

        public void setChildren(List<Location> children) {
            this.children = children;
        }
    }
}
