package com.lingdonge.http.faker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lingdonge.core.dates.DateUtil;
import com.lingdonge.core.http.HttpHelper;
import com.lingdonge.core.bean.common.ModelMobileLocation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码归属地接口查询
 * Created by kyle on 17/5/5.
 */
@Slf4j
public class MobileLocationHelper {

    public static void main(String[] args) {

        try {
            ModelMobileLocation location = getPhoneLocationByShowJi("18088313124");
            log.info(location.toString());

//            location = getPhoneLocationByTaobao("13429667914");
//            log.info(location.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取手机号码归属地并返回结果，会自动轮询使用已经存在的2个接口
     *
     * @param mobile
     * @return
     * @throws IOException
     */
    public static ModelMobileLocation getMobileLocation(String mobile) throws IOException {

        ModelMobileLocation mobileModel = getPhoneLocationByJuhe(mobile);

        if (mobileModel == null) {
            mobileModel = getPhoneLocationByShowJi(mobile);//优先使用这个，可以查具体的城市
        }

        if (mobileModel == null) {
            mobileModel = getPhoneLocationByTaobao(mobile);
        }

        return mobileModel;
    }


    private static String juheAPIKey = "12bcb31572831cc9fb3b2dfdf200d062";

    /**
     * 使用聚合数据查询IP地址归属地信息
     * https://www.juhe.cn/docs/api/id/11
     *
     * @param phone
     * @return
     */
    public static ModelMobileLocation getPhoneLocationByJuhe(String phone) throws IOException {
        phone = phone.trim();

        ModelMobileLocation model = null;

        if (StringUtils.isEmpty(phone)) {
            log.error("getPhoneLocationByJuhe手机号码不能为空");
        } else {

            final HttpHelper http = new HttpHelper();

            String html = http.getHtml("http://apis.juhe.cn/mobile/get?phone=" + phone.trim() + "&key=" + juheAPIKey);

            JSONObject object = JSON.parseObject(html);
            if (object.getInteger("error_code") == 0) {

                model = new ModelMobileLocation(phone);

                JSONObject result = object.getJSONObject("result");

                model.setProvince(result.getString("province"));
                model.setCity(result.getString("city"));
                model.setAreacode(result.getString("areacode"));
                model.setCard(result.getString("card"));
                model.setIsp(result.getString("company"));
                model.setZip(result.getString("zip"));
            } else {
                log.error("getPhoneLocationByJuhe Trigger Error:" + object.get("error_code") + ":" + object.get("reason"));
            }

        }

        return model;
    }


    /**
     * 手机号码归属地查询：http://www.showji.com
     *
     * @param phone
     */
    public static ModelMobileLocation getPhoneLocationByShowJi(String phone) throws IOException {

        ModelMobileLocation model = null;

        if (StringUtils.isEmpty(phone)) {
            log.error("手机号码不能为空");

        } else {
            phone = phone.trim();

            final HttpHelper http = new HttpHelper();

            String ret = http.getHtml(MessageFormat.format("http://v.showji.com/Locating/showji.com2016234999234.aspx?m={0}&output=json&callback=querycallback&timestamp={1}", phone, DateUtil.nowUnixTimestamp()));

//        log.info(ret);

            final String regex = "\\{([\\s\\S]*?)\\}";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(ret);

            if (matcher.find()) {
                JSONObject json = JSON.parseObject(matcher.group(0));
                model = new ModelMobileLocation(phone);
                model.setProvince(json.getString("Province"));
                model.setIsp(json.getString("Corp"));
                model.setCity(json.getString("City"));
                model.setAreacode(json.getString("AreaCode"));
                model.setZip(json.getString("PostCode"));

            } else {
                log.info("未找到匹配项");
            }
        }

        return model;

    }

    /**
     * 未开发完成，使用IP138查询，需要解析里面的数据，暂时未完成
     *
     * @param phone
     * @return
     * @throws IOException
     */
    public static ModelMobileLocation getPhoneLocationByIP138(String phone) throws IOException {

        ModelMobileLocation model = null;

        if (StringUtils.isEmpty(phone)) {
            log.error("手机号码不能为空");

        } else {
            phone = phone.trim();

            final HttpHelper http = new HttpHelper();

            String ret = http.getHtml(MessageFormat.format("http://www.ip138.com:8080/search.asp?mobile={0}&action=mobile", phone.trim()));

//        log.info(ret);

            final String regex = "\\{([\\s\\S]*?)\\}";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(ret);

            if (matcher.find()) {
                JSONObject json = JSON.parseObject(matcher.group(0));
                model = new ModelMobileLocation(phone);
                model.setProvince(json.getString("province"));
                model.setIsp(json.getString("carrier"));

            } else {
                log.info("未找到匹配项");
            }
        }

        return model;

    }

    /**
     * 手机号码归属地查询：淘宝接口,不建议，只能查到省份
     *
     * @param phone
     */
    public static ModelMobileLocation getPhoneLocationByTaobao(String phone) throws IOException {

        ModelMobileLocation model = null;

        if (StringUtils.isEmpty(phone)) {
            log.error("手机号码不能为空");

        } else {
            phone = phone.trim();

            final HttpHelper http = new HttpHelper();

            String ret = http.getHtml("https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + phone);

//        log.info(ret);

            final String regex = "\\{([\\s\\S]*?)\\}";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(ret);

            if (matcher.find()) {
                JSONObject json = JSON.parseObject(matcher.group(0));
                model = new ModelMobileLocation(phone);
                model.setProvince(json.getString("province"));
                model.setIsp(json.getString("carrier"));

            } else {
                log.info("未找到匹配项");
            }
        }

        return model;

    }
}
