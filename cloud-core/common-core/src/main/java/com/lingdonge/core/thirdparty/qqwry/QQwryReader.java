package com.lingdonge.core.thirdparty.qqwry;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description:ip文件转化，将原有的数据根式转化为新的格式文件、也可以读取所有IP地址信息的记录 新格式如下：
 * +----------+
 * |   文件头       |  (8字节)
 * +----------+
 * |   记录区       |  (不定长)
 * +----------+
 * |   索引区       |  (大小由文件头决定)
 * +----------+
 * 文件头：
 * +------------------------------+-----------------------------+
 * | first index position(4 bytes)|last index position(4 bytes) |
 * +------------------------------+-----------------------------+
 * 记录区：
 * +------------------+----------+------------------+----------+-----
 * | country1(n bytes)|\0(1 byte)| country2(n bytes)|\0(1 byte)|...
 * +------------------+----------+------------------+----------+-----
 * +------------------+----------+------------------+----------+-----
 * | area1(n bytes)   |\0(1 byte)| area2(n bytes)   |\0(1 byte)|...
 * +------------------+----------+------------------+----------+-----
 * 索引区：
 * +------------+-------------------------+------------------------+
 * |ip1(4 bytes) |country position(3 bytes)| area position(3 bytes)|...
 * +------------+-------------------------+------------------------+
 * @author:difeng
 * @date:2016年12月16日
 */
public class QQwryReader {

    private byte[] data;

    private long firstIndexOffset;

    private long lastIndexOffset;

    private long totalIndexCount;

    private static final byte REDIRECT_MODE_1 = 0x01;

    private static final byte REDIRECT_MODE_2 = 0x02;

    static final long IP_RECORD_LENGTH = 7;

    private static ReentrantLock lock = new ReentrantLock();

    public static boolean enableFileWatch = false;

    private File origionalQQwryFile;

    private File newFormatFile;

    public QQwryReader(String origionalQQwryFilePath) throws Exception {
        this.origionalQQwryFile = new File(origionalQQwryFilePath);
        load();
    }

    /**
     * 读取文件的起始位置存为全局变量
     *
     * @throws Exception
     */
    private void load() throws Exception {
        ByteArrayOutputStream out = null;
        FileInputStream in = null;
        lock.lock();
        try {
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            in = new FileInputStream(origionalQQwryFile);
            while (in.read(b) != -1) {
                out.write(b);
            }
            data = out.toByteArray();
            firstIndexOffset = read4ByteAsLong(0);
            lastIndexOffset = read4ByteAsLong(4);
            totalIndexCount = (lastIndexOffset - firstIndexOffset) / IP_RECORD_LENGTH + 1;
            in.close();
            out.close();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            lock.unlock();
        }
    }

    private long read4ByteAsLong(final int offset) {
        long val = data[offset] & 0xFF;
        val |= (data[offset + 1] << 8L) & 0xFF00L;
        val |= (data[offset + 2] << 16L) & 0xFF0000L;
        val |= (data[offset + 3] << 24L) & 0xFF000000L;
        return val;
    }

    private long read3ByteAsLong(final int offset) {
        long val = data[offset] & 0xFFL;
        val |= (data[offset + 1] << 8) & 0xFF00L;
        val |= (data[offset + 2] << 16) & 0xFF0000L;
        return val;
    }

    private long search(long ip) {
        long low = 0;
        long high = totalIndexCount;
        long mid = 0;
        while (low <= high) {
            mid = (low + high) >>> 1;
            long indexIP = read4ByteAsLong((int) (firstIndexOffset + (mid - 1) * IP_RECORD_LENGTH));
            long indexIPNext = read4ByteAsLong((int) (firstIndexOffset + mid * IP_RECORD_LENGTH));
            if (indexIP <= ip && ip < indexIPNext) {
                return read3ByteAsLong((int) (firstIndexOffset + (mid - 1) * IP_RECORD_LENGTH + 4));
            } else if (ip == indexIPNext) {
                return read3ByteAsLong((int) (firstIndexOffset + mid * IP_RECORD_LENGTH + 4));
            } else {
                if (ip > indexIP) {
                    low = mid + 1;
                } else if (ip < indexIP) {
                    high = mid - 1;
                }
            }
        }
        return -1;
    }

    public Location location(String ip) {
        long numericIp = inet_pton(ip);
        lock.lock();
        long offset = search(numericIp);
        try {
            if (offset != -1) {
                return fetchLocation((int) offset);
            }
        } finally {
            lock.unlock();
        }
        return null;
    }

    private Location fetchLocation(final int offset) {
        Location loc = new Location();
        try {
            byte redirectMode = data[offset + 4];
            if (redirectMode == REDIRECT_MODE_1) {
                long countryOffset = read3ByteAsLong((int) offset + 5);
                redirectMode = data[(int) countryOffset];
                if (redirectMode == REDIRECT_MODE_2) {
                    final QQwryString country = readString((int) read3ByteAsLong((int) countryOffset + 1));
                    loc.country = country.string;
                    countryOffset = countryOffset + 4;
                } else {
                    final QQwryString country = readString((int) countryOffset);
                    loc.country = country.string;
                    countryOffset += country.byteCount;
                }
                loc.area = readArea((int) countryOffset);
            } else if (redirectMode == REDIRECT_MODE_2) {
                loc.country = readString((int) read3ByteAsLong((int) offset + 5)).string;
                loc.area = readArea((int) offset + 8);
            } else {
                final QQwryString country = readString((int) offset + 4);
                loc.country = country.string;
                loc.area = readArea((int) offset + 4 + country.byteCount);
            }
            return loc;
        } catch (Exception e) {
            return null;
        }
    }

    private String readArea(final int offset) {
        byte redirectMode = data[offset];
        if (redirectMode == REDIRECT_MODE_1 || redirectMode == REDIRECT_MODE_2) {
            long areaOffset = read3ByteAsLong((int) offset + 1);
            if (areaOffset == 0) {
                return "";
            } else {
                return readString((int) areaOffset).string;
            }
        } else {
            return readString(offset).string;
        }
    }

    private QQwryString readString(final int offset) {
        int pos = offset;
        final byte[] b = new byte[256];
        int i;
        for (i = 0, b[i] = data[pos++]; b[i] != 0; b[++i] = data[pos++]) ;

        try {
            return new QQwryString(new String(b, 0, i, "GBK"), i + 1);
        } catch (UnsupportedEncodingException e) {
            return new QQwryString("", 0);
        }
    }

    /**
     * @param ipStr
     * @return t
     * @Description:“.”号分隔的字符串转换为long类型的数字
     * @return:long
     */
    private static long inet_pton(String ipStr) {
        if (ipStr == null) {
            throw new NullPointerException("ip不能为空");
        }
        String[] arr = ipStr.split("\\.");
        long ip = (Long.parseLong(arr[0]) & 0xFFL) << 24 & 0xFF000000L;
        ip |= (Long.parseLong(arr[1]) & 0xFFL) << 16 & 0xFF0000L;
        ip |= (Long.parseLong(arr[2]) & 0xFFL) << 8 & 0xFF00L;
        ip |= (Long.parseLong(arr[3]) & 0xFFL);
        return ip;
    }

    private class QQwryString {

        public final String string;

        public final int byteCount;

        public QQwryString(final String string, final int byteCount) {
            this.string = string;
            this.byteCount = byteCount;
        }

        @Override
        public String toString() {
            return string;
        }
    }


    /**
     * 读取所有数据，转换为IP信息
     *
     * @throws Exception
     */
    public List<Location> readAll() throws Exception {
        final long start = firstIndexOffset;
        final long count = totalIndexCount;
        //the ip list,order asc
        List<Location> locList = new ArrayList<Location>();
//        Set<String> countrySet = new HashSet<String>();
//        Set<String> areaSet = new HashSet<String>();
        //read the ip information from index region
        for (int i = 0; i < count; i++) {
            final long ipPos = read4ByteAsLong((int) (start + i * IP_RECORD_LENGTH));
            final Location loc = fetchLocation((int) read3ByteAsLong((int) (start + i * IP_RECORD_LENGTH + 4)));
//			loc.changeEnCode();
            loc.ip = inet_ntoa(ipPos);
            locList.add(loc);
//            countrySet.add(loc.country);
//            areaSet.add(loc.area);
        }

        return locList;
    }

    /**
     * 读取指定城市下面的IP地址
     *
     * @param cityName
     * @return
     */
    public List<Location> readByCity(String cityName) {
        final long start = firstIndexOffset;
        final long count = totalIndexCount;
        //the ip list,order asc
        List<Location> locList = new ArrayList<Location>();
//        Set<String> countrySet = new HashSet<String>();
//        Set<String> areaSet = new HashSet<String>();
        //read the ip information from index region
        for (int i = 0; i < count; i++) {
            final long ipPos = read4ByteAsLong((int) (start + i * IP_RECORD_LENGTH));
            final Location loc = fetchLocation((int) read3ByteAsLong((int) (start + i * IP_RECORD_LENGTH + 4)));
//			loc.changeEnCode();

            //满足条件的数据才会采集到列表里面来
            if (loc.country.contains(cityName)) {
                loc.ip = inet_ntoa(ipPos);
                locList.add(loc);
//            countrySet.add(loc.country);
//            areaSet.add(loc.area);
            }

        }

        return locList;
    }

    /**
     * 转换为新的格式
     *
     * @throws Exception
     */
    public void convert(String newFormatFilePath) throws Exception {

        this.newFormatFile = new File(newFormatFilePath);


        final long start = firstIndexOffset;
        final long count = totalIndexCount;
        //the ip list,order asc
        List<Location> locList = new ArrayList<Location>();
        Set<String> countrySet = new HashSet<String>();
        Set<String> areaSet = new HashSet<String>();
        //read the ip information from index region
        for (int i = 0; i < count; i++) {
            final long ipPos = read4ByteAsLong((int) (start + i * IP_RECORD_LENGTH));
            final Location loc = fetchLocation((int) read3ByteAsLong((int) (start + i * IP_RECORD_LENGTH + 4)));
//			loc.changeEnCode();
            loc.ip = inet_ntoa(ipPos);
            locList.add(loc);
            countrySet.add(loc.country);
            areaSet.add(loc.area);
        }
        //the new ip file data array
        byte[] arr = new byte[1024 * 1024 * 10];
        //0-3:first index position
        //4-7:last index position
        //begin position to write the country and area data
        int pos = 8;
        //country->country info position
        Map<String, Integer> countryIndex = new HashMap<String, Integer>();
        //area->area info position
        Map<String, Integer> areaIndex = new HashMap<String, Integer>();

        //write country info from current position
        for (String country : countrySet) {
            try {
                country = country.trim();
                countryIndex.put(country, pos);
                System.arraycopy(country.getBytes("GBK"), 0, arr, pos, country.getBytes("GBK").length);
                pos += country.getBytes("GBK").length;
                //write end char
                arr[pos++] = '\0';
            } catch (Exception e) {
                System.out.println(pos);
                System.exit(0);
            }
        }

        //write area info from current position
        for (String area : areaSet) {
            try {
                area = area.trim();
                areaIndex.put(area, pos);
                System.arraycopy(area.getBytes("GBK"), 0, arr, pos, area.getBytes("GBK").length);
                pos += area.getBytes("GBK").length;
                //write end char
                arr[pos++] = '\0';
            } catch (Exception e) {
                System.out.println(pos);
                System.exit(0);
            }
        }
        //record the index start position
        int indexS = pos;
        //index write
        for (Location loc : locList) {
            int ipPos = pos;
            int index1 = countryIndex.get(loc.country.trim());
            int index2 = areaIndex.get(loc.area.trim());
            long ip = inet_pton(loc.ip);
            //write ip
            ip = ip & 0xFFFFFFFF;
            arr[pos++] = (byte) (ip & 0xFF);
            arr[pos++] = (byte) (ip >> 8 & 0xFF);
            arr[pos++] = (byte) (ip >> 16 & 0xFF);
            arr[pos++] = (byte) (ip >> 24 & 0xFF);

            //write country position
            index1 = index1 & 0xFFFFFFFF;
            arr[pos++] = (byte) (index1 & 0xFF);
            arr[pos++] = (byte) (index1 >> 8 & 0xFF);
            arr[pos++] = (byte) (index1 >> 16 & 0xFF);

            //write area position
            index2 = index2 & 0xFFFFFFFF;
            arr[pos++] = (byte) (index2 & 0xFF);
            arr[pos++] = (byte) (index2 >> 8 & 0xFF);
            arr[pos++] = (byte) (index2 >> 16 & 0xFF);
            System.out.println(loc.ip + ":" + ipPos + ":" + index1 + ":" + index2);
        }
        //record the last index position
        int indexL = pos - 10;
        System.out.println("indexS:" + indexS);
        System.out.println("indexL:" + indexL);
        System.out.println("length:" + (pos - 1));

        //write the first ip index position
        arr[0] = (byte) (indexS & 0xFF);
        arr[1] = (byte) (indexS >> 8 & 0xFF);
        arr[2] = (byte) (indexS >> 16 & 0xFF);
        arr[3] = (byte) (indexS >> 24 & 0xFF);

        //write the last ip index position
        arr[4] = (byte) (indexL & 0xFF);
        arr[5] = (byte) (indexL >> 8 & 0xFF);
        arr[6] = (byte) (indexL >> 16 & 0xFF);
        arr[7] = (byte) (indexL >> 24 & 0xFF);

        //write byte array arr into newFormatFile
        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(newFormatFile);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(arr, 0, pos - 1);
            out.writeTo(fileOut);
            out.close();
            fileOut.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param ip
     * @return
     * @Description:将long类型的ip转换为“.”分隔的ip串
     * @time:2016年11月23日 下午4:41:11
     * @return:String
     */
    public static String inet_ntoa(long ip) {
        String fmtIp = "";
        fmtIp += String.valueOf(ip >> 24 & 0xff).concat(".");
        fmtIp += String.valueOf(ip >> 16 & 0xff).concat(".");
        fmtIp += String.valueOf(ip >> 8 & 0xff).concat(".");
        fmtIp += String.valueOf(ip & 0xff);
        return fmtIp;
    }
}
