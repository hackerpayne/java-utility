package com.lingdonge.spider.thirdparty;

import com.lingdonge.core.bean.common.ModelIPLocation;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.*;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * MaxMind本地操作类
 * 官方GitHub https://github.com/maxmind/GeoIP2-java
 * Created by Kyle on 16/8/26.
 */
@Slf4j
public class MaxMindGeoHelper {
    public static void main(String[] args) {
        String pat = System.getProperty("user.dir");

        MaxMindGeoHelper max = new MaxMindGeoHelper();

        ModelIPLocation loc = max.parseIP("103.3.120.2");

        System.out.println(loc);
    }


    private File cityDatabase;
    private File ipDatabase;
    private File connectTypeDatabase;
    private File domainDatabase;
    private File ispDatabase;

    DatabaseReader readerCity;
    DatabaseReader readerIP;
    DatabaseReader readerConnectionType;
    DatabaseReader readerDomain;
    DatabaseReader readerIsp;

    /**
     * 构造函数，初始化路径配置
     */
    public MaxMindGeoHelper() {
        String sysPath = System.getProperty("user.dir") + File.separator + "conf" + File.separator;

//        System.out.println(sysPath);

        this.cityDatabase = new File(sysPath + "GeoLite2-City.mmdb");
        this.ipDatabase = new File(sysPath + "GeoIP2-City.mmdb");
        this.connectTypeDatabase = new File(sysPath + "GeoIP2-City.mmdb");
        this.domainDatabase = new File(sysPath + "GeoIP2-City.mmdb");
        this.ispDatabase = new File(sysPath + "GeoIP2-City.mmdb");
    }

    /**
     * 解析IP地址为城市、省、国家信息
     *
     * @param ipAddr
     * @return
     */
    public ModelIPLocation parseIP(String ipAddr) {

        ModelIPLocation location = new ModelIPLocation();

        location.setIp(ipAddr.trim());

        if (StringUtils.isEmpty(ipAddr)) {
            return location;
        }

        try {

            if (readerCity == null && cityDatabase.exists()) {
                readerCity = new DatabaseReader.Builder(cityDatabase).build();
            }

            InetAddress ipAddress = InetAddress.getByName(ipAddr);

            CityResponse cityResponse = readerCity.city(ipAddress);

            if (cityResponse != null) {
                Country country = cityResponse.getCountry();
                location.setCountry(country.getNames().get("zh-CN"));

                Subdivision subdivision = cityResponse.getMostSpecificSubdivision();
                location.setProvince(subdivision.getNames().get("zh-CN"));

                City city = cityResponse.getCity();
                location.setCity(city.getNames().get("zh-CN"));

            }

            if (ispDatabase.exists()) {
                if (readerIsp == null) {
                    readerIsp = new DatabaseReader.Builder(ispDatabase).build();
                }

                IspResponse ispResponse = readerIsp.isp(ipAddress);
                if (ispResponse != null) {
                    location.setIsp(ispResponse.getIsp());
                }
            }


        } catch (Exception e) {
            log.error("", e);
        }
        return location;
    }


    public CityResponse parseCity(String ipAddr) {


        try {

            if (readerCity == null) {
                readerCity = new DatabaseReader.Builder(cityDatabase).build();
            }

            InetAddress ipAddress = InetAddress.getByName(ipAddr);

            CityResponse response = readerCity.city(ipAddress);

            //Country country = response.getCountry();

            return response;

        } catch (GeoIp2Exception e) {
            log.error("", e);
        } catch (UnknownHostException e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        }

        return null;
    }

    public void parseAnonymoutIP(String ipAddr) {

        try {
            if (readerIP == null) {
                readerIP = new DatabaseReader.Builder(ipDatabase).build();
            }

            InetAddress ipAddress = InetAddress.getByName(ipAddr);

            AnonymousIpResponse response = readerIP.anonymousIp(ipAddress);

            System.out.println(response.isAnonymous()); // true
            System.out.println(response.isAnonymousVpn()); // false
            System.out.println(response.isHostingProvider()); // false
            System.out.println(response.isPublicProxy()); // false
            System.out.println(response.isTorExitNode()); //true

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeoIp2Exception e) {
            e.printStackTrace();
        }
    }

    public void parseConnectionType(String ipAddr) {

        try {
            if (readerConnectionType == null) {
                readerConnectionType = new DatabaseReader.Builder(connectTypeDatabase).build();
            }

            InetAddress ipAddress = InetAddress.getByName(ipAddr);

            ConnectionTypeResponse response = readerConnectionType.connectionType(ipAddress);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeoIp2Exception e) {
            e.printStackTrace();
        }

    }

    public void parseDomain(String ipAddr) {

        try {
            if (readerDomain == null) {
                readerDomain = new DatabaseReader.Builder(domainDatabase).build();
            }

            InetAddress ipAddress = InetAddress.getByName(ipAddr);

            DomainResponse response = readerDomain.domain(ipAddress);

            System.out.println(response.getDomain()); // 'Corporate'

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeoIp2Exception e) {
            e.printStackTrace();
        }

    }

    public void parseIsp(String ipAddr) {

        try {
            if (readerIsp == null) {
                readerIsp = new DatabaseReader.Builder(ispDatabase).build();
            }

            InetAddress ipAddress = InetAddress.getByName(ipAddr);

            IspResponse response = readerIsp.isp(ipAddress);

            System.out.println(response.getAutonomousSystemNumber());       // 217
            System.out.println(response.getAutonomousSystemOrganization()); // 'University of Minnesota'
            System.out.println(response.getIsp());                          // 'University of Minnesota'
            System.out.println(response.getOrganization());                 // 'University of Minnesota'

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeoIp2Exception e) {
            e.printStackTrace();
        }

    }

}
