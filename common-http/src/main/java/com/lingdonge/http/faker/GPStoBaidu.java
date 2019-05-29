package com.lingdonge.http.faker;

import com.jayway.jsonpath.JsonPath;
import com.lindonge.core.http.HttpHelper;
import com.lindonge.core.util.NumberUtil;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * GPS经纬度转换
 */
public class GPStoBaidu {

    /**
     * 百度TOken
     */
    public static String baiduAkToken = "MTrIsro5y5NWoyFsphqGl00q";

    /**
     * GPS经纬度转换为百度墨卡托坐标
     *
     * @param langtitude
     * @param longtitude
     * @return
     */
    public static String[] gpsToBaiduMC(String langtitude, String longtitude) {
        HttpHelper http = new HttpHelper();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("coords", langtitude + "," + longtitude);//普通经纬度
        paramMap.put("ak", baiduAkToken);
        paramMap.put("to", "3");//GPS设备获取的角度坐标，wgs84坐标;
        paramMap.put("to", "6");//代表bd09mc(百度米制经纬度坐标,即墨卡托坐标);
        String post = http.postHtml("http://api.map.baidu.com/geoconv/v1/?", paramMap);
//        System.out.println(post);
        return new String[]{parseDouble(JsonPath.read(post, "$.result[0].x"), 5), parseDouble(JsonPath.read(post, "$.result[0].y"), 5)};
    }

    private static String parseDouble(double number, Integer length) {
        BigDecimal b = new BigDecimal(Double.valueOf(number));
        return b.setScale(length, BigDecimal.ROUND_HALF_UP).toString();

//        NumberFormat nf = NumberFormat.getInstance();
//        // 是否以逗号隔开, 默认true以逗号隔开,如[123,456,789.128]
//        nf.setGroupingUsed(false);
//        // 结果未做任何处理
//        String value= nf.format(number);
//        value = value.replaceAll(",", "");

    }

    /**
     * 百度墨卡托转换为国测局（gcj02）坐标
     *
     * @param langtitude
     * @param longtitude
     * @return
     */
    public static double[] baiduMCToGps(String langtitude, String longtitude) {
        HttpHelper http = new HttpHelper();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("coords", langtitude + "," + longtitude);//普通经纬度
        paramMap.put("from", "6");//代表bd09mc(百度米制经纬度坐标,即墨卡托坐标);
        paramMap.put("ak", baiduAkToken);
        paramMap.put("to", "3");
        String post = http.postHtml("http://api.map.baidu.com/geoconv/v1/?", paramMap);
        return new double[]{NumberUtil.formatDouble(JsonPath.read(post, "$.result[0].x"), 5), NumberUtil.formatDouble(JsonPath.read(post, "$.result[0].y"), 5)};
    }


    /**
     * 普通经纬度转百度经纬度
     *
     * @param lat
     * @param lng
     * @return
     */
    public static double[] postBaidu(double lat, double lng) {
        double[] latlng = null;

        URL url = null;
        URLConnection connection = null;
        try {
            url = new URL("http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + String.valueOf(lat) + "&y=" + String.valueOf(lng));
            connection = url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
            out.flush();
            out.close();

            // 服务器的回应的字串，并解析
            String sCurrentLine;
            String sTotalString;
            sCurrentLine = "";
            sTotalString = "";
            InputStream l_urlStream;
            l_urlStream = connection.getInputStream();
            BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
            while ((sCurrentLine = l_reader.readLine()) != null) {
                if (!sCurrentLine.equals(""))
                    sTotalString += sCurrentLine;
            }
            // System.out.println(sTotalString);
            sTotalString = sTotalString.substring(1, sTotalString.length() - 1);
            // System.out.println(sTotalString);
            String[] results = sTotalString.split("\\,");
            if (results.length == 3) {
                if (results[0].split("\\:")[1].equals("0")) {
                    String mapX = results[1].split("\\:")[1];
                    String mapY = results[2].split("\\:")[1];
                    mapX = mapX.substring(1, mapX.length() - 1);
                    mapY = mapY.substring(1, mapY.length() - 1);
                    mapX = new String(Base64.decode(mapX));
                    mapY = new String(Base64.decode(mapY));
                    // System.out.println(mapX);
                    // System.out.println(mapY);
                    latlng = new double[]{Double.parseDouble(mapX), Double.parseDouble(mapY)};
                } else {
                    System.out.println("error != 0");
                }
            } else {
                System.out.println("String invalid!");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("GPS转百度坐标异常！");
        }
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
//        log.info("百度GPS===" + dateFormat1.format(new Date()) + " " + latlng[0] + " " + latlng[1]);
        return latlng;
    }

    public static void main(String[] args) throws IOException {
//        double lat = 114.42285333333334;
//        double lng = 30.459873333333334;
//        double[] latlng = GPStoBaidu.postBaidu(lat, lng);
//        System.out.println("lat===" + latlng[0] + "  lng===" + latlng[1]);

        //墨卡托经纬度转换
//        double[] num;
//        num = lonLat2Mercator(116.36477, 39.87184);
//        for (int i = 0; i < num.length; i++) {
//            System.out.println(num[i]);
//
//
//        }

//        double[] gps11 = baiduMCToGps("12965082.34", "4823689.86");
//        System.out.println(gps11[0]);
//        System.out.println(gps11[1]);
//
//        String[] gps = gpsToBaiduMC("116.412003", "39.914396");
//        System.out.println(gps[0]);
//        System.out.println(gps[1]);


//          num = Mercator2lonLat(13401221.612075035,4309075.414032666);
//          for(int i=0;i<num.length;i++)
//          {
//           System.out.println(num[i]);
//          }
    }
}