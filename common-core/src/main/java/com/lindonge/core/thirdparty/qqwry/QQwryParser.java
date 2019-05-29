package com.lindonge.core.thirdparty.qqwry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;

/**
 * qqwry.dat解析类
 */
public class QQwryParser {
    private static final Logger logger = LoggerFactory.getLogger(QQwryParser.class);

    // 一些固定常量，比如记录长度等等
    private static final int IP_RECORD_LENGTH = 7;
    private static final byte AREA_FOLLOWED = 0x01;
    private static final byte NO_AREA = 0x2;

    // 随机文件访问类
    private RandomAccessFile qqwryFile;

    // 内存映射文件
    private MappedByteBuffer mbb;

    // 起始地区的开始和结束的绝对偏移
    private long ipBegin, ipEnd;

    private byte[] buf = new byte[100];
    ;
    private byte[] b4 = new byte[4];
    private byte[] b3 = new byte[3];

    /**
     * 打开文件进行解析
     * @param file
     * @throws Exception
     */
    public QQwryParser(File file) throws Exception {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        qqwryFile = new RandomAccessFile(file, "r");
        // 如果打开文件成功，读取文件头信息
        try {
            ipBegin = readLong4(0);
            ipEnd = readLong4(4);
            if (ipBegin == -1 || ipEnd == -1) {
                qqwryFile.close();
                qqwryFile = null;
                logger.error("IP地址信息文件格式有错误.");
            }
        } catch (IOException e) {
            qqwryFile.close();
            qqwryFile = null;
            logger.error("IP地址信息文件格式有错误.");
            throw e;
        }

    }

    /**
     * 关闭文件
     */
    public void close() {
        try {
            qqwryFile.close();
            qqwryFile = null;
        } catch (IOException e) {

        }
    }

    int readInt3(int offset) {
        mbb.position(offset);
        return mbb.getInt() & 0x00FFFFFF;
    }

    /**
     * 读取IP地址信息
     * @param ip
     * @return
     */
    public IPAddrInfo getAddrInfo(String ip) {
        // 检查ip地址文件是否正常
        if (qqwryFile == null) {
            return null;
        }

        IPAddrInfo IPAddrInfo = getElement(IPEncode.encode(ip));
        return IPAddrInfo;
    }


    /**
     * 根据ip搜索ip信息文件，得到IPLocation结构，所搜索的ip参数从类成员ip中得到
     *
     * @param ipAddr 要查询的IP
     * @return IPLocation结构
     */
    private IPAddrInfo getElement(byte[] ipAddr) {
        IPAddrInfo info = null;
        long offset = findOffset(ipAddr);
        if (offset != -1)
            info = readElement(offset);
        if (info == null) {
            info = new IPAddrInfo();
            info.setProvider("未知");
        }
        try {
            info.setAddress(InetAddress.getByAddress(ipAddr));
        } catch (UnknownHostException e) {
            logger.warn(e.getMessage(), e);
        }
        return info;
    }

    /**
     * 从offset位置读取4个字节为一个long，因为java为big-endian格式，所以没办法 用了这么一个函数来做转换
     *
     * @param offset
     * @return 读取的long值，返回-1表示读取文件失败
     */
    private long readLong4(long offset) {
        long ret = 0;
        try {
            qqwryFile.seek(offset);
            ret |= (qqwryFile.readByte() & 0xFF);
            ret |= ((qqwryFile.readByte() << 8) & 0xFF00);
            ret |= ((qqwryFile.readByte() << 16) & 0xFF0000);
            ret |= ((qqwryFile.readByte() << 24) & 0xFF000000);
            return ret;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 从offset位置读取3个字节为一个long，因为java为big-endian格式，所以没办法 用了这么一个函数来做转换
     *
     * @param offset
     * @return 读取的long值，返回-1表示读取文件失败
     */
    private long readLong3(long offset) {
        long ret = 0;
        try {
            qqwryFile.seek(offset);
            qqwryFile.readFully(b3);
            ret |= (b3[0] & 0xFF);
            ret |= ((b3[1] << 8) & 0xFF00);
            ret |= ((b3[2] << 16) & 0xFF0000);
            return ret;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 从当前位置读取3个字节转换成long
     *
     * @return
     */
    private long readLong3() {
        long ret = 0;
        try {
            qqwryFile.readFully(b3);
            ret |= (b3[0] & 0xFF);
            ret |= ((b3[1] << 8) & 0xFF00);
            ret |= ((b3[2] << 16) & 0xFF0000);
            return ret;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 从offset位置读取四个字节的ip地址放入ip数组中，读取后的ip为big-endian格式，但是
     * 文件中是little-endian形式，将会进行转换
     *
     * @param offset
     * @param ip
     */
    private void readIP(long offset, byte[] ip) {
        try {
            qqwryFile.seek(offset);
            qqwryFile.readFully(ip);
            byte temp = ip[0];
            ip[0] = ip[3];
            ip[3] = temp;
            temp = ip[1];
            ip[1] = ip[2];
            ip[2] = temp;
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 从offset位置读取四个字节的ip地址放入ip数组中，读取后的ip为big-endian格式，但是
     * 文件中是little-endian形式，将会进行转换
     *
     * @param offset
     * @param ip
     */
    void readIP(int offset, byte[] ip) {
        mbb.position(offset);
        mbb.get(ip);
        byte temp = ip[0];
        ip[0] = ip[3];
        ip[3] = temp;
        temp = ip[1];
        ip[1] = ip[2];
        ip[2] = temp;
    }

    /**
     * 把类成员ip和beginIp比较，注意这个beginIp是big-endian的
     *
     * @param ip      要查询的IP
     * @param beginIp 和被查询IP相比较的IP
     * @return 相等返回0，ip大于beginIp则返回1，小于返回-1。
     */
    private int compareIP(byte[] ip, byte[] beginIp) {
        for (int i = 0; i < 4; i++) {
            int r = compareByte(ip[i], beginIp[i]);
            if (r != 0)
                return r;
        }
        return 0;
    }

    /**
     * 把两个byte当作无符号数进行比较
     *
     * @param b1
     * @param b2
     * @return 若b1大于b2则返回1，相等返回0，小于返回-1
     */
    private int compareByte(byte b1, byte b2) {
        if ((b1 & 0xFF) > (b2 & 0xFF)) // 比较是否大于
            return 1;
        else if ((b1 ^ b2) == 0)// 判断是否相等
            return 0;
        else
            return -1;
    }

    /**
     * 这个方法将根据ip的内容，定位到包含这个ip国家地区的记录处，返回一个绝对偏移 方法使用二分法查找。
     *
     * @param ip 要查询的IP
     * @return 如果找到了，返回结束IP的偏移，如果没有找到，返回-1
     */
    private long findOffset(byte[] ip) {
        long m = 0;
        int r;
        // 比较第一个ip项
        readIP(ipBegin, b4);
        r = compareIP(ip, b4);
        if (r == 0)
            return ipBegin;
        else if (r < 0)
            return -1;
        // 开始二分搜索
        for (long i = ipBegin, j = ipEnd; i < j; ) {
            m = getMiddleOffset(i, j);
            readIP(m, b4);
            r = compareIP(ip, b4);
            // log.debug(Utils.getIpStringFromBytes(b));
            if (r > 0)
                i = m;
            else if (r < 0) {
                if (m == j) {
                    j -= IP_RECORD_LENGTH;
                    m = j;
                } else
                    j = m;
            } else
                return readLong3(m + 4);
        }
        // 如果循环结束了，那么i和j必定是相等的，这个记录为最可能的记录，但是并非
        // 肯定就是，还要检查一下，如果是，就返回结束地址区的绝对偏移
        m = readLong3(m + 4);
        readIP(m, b4);
        r = compareIP(ip, b4);
        if (r <= 0)
            return m;
        else
            return -1;
    }

    /**
     * 得到begin偏移和end偏移中间位置记录的偏移
     *
     * @param begin
     * @param end
     * @return
     */
    private long getMiddleOffset(long begin, long end) {
        long records = (end - begin) / IP_RECORD_LENGTH;
        records >>= 1;
        if (records == 0)
            records = 1;
        return begin + records * IP_RECORD_LENGTH;
    }

    private void setAreaInfo(IPAddrInfo e, String areaInfo) {
        e.setCountry(AreaUtils.getCountry(areaInfo));
        String province = AreaUtils.getProvince(areaInfo);
        e.setProvince(province == null ? "未知" : province);
        String city = AreaUtils.getCity(areaInfo);
        e.setCity(city == null ? "未知" : city);
    }

    /**
     * 给定一个ip国家地区记录的偏移，返回一个Element结构
     *
     * @param offset
     * @return
     */
    private IPAddrInfo readElement(long offset) {
        try {
            IPAddrInfo IPAddrInfo = new IPAddrInfo();
            String areaInfo = "";
            // 跳过4字节ip
            qqwryFile.seek(offset + 4);
            // 读取第一个字节判断是否标志字节
            byte b = qqwryFile.readByte();
            if (b == AREA_FOLLOWED) {
                // 读取国家偏移
                long countryOffset = readLong3();
                // 跳转至偏移处
                qqwryFile.seek(countryOffset);
                // 再检查一次标志字节，因为这个时候这个地方仍然可能是个重定向
                b = qqwryFile.readByte();
                if (b == NO_AREA) {
                    areaInfo = (readString(readLong3()));
                    qqwryFile.seek(countryOffset + 4);
                } else {
                    areaInfo = (readString(countryOffset));
                }
                // 读取地区标志
                IPAddrInfo.setProvider(readArea(qqwryFile.getFilePointer()));
            } else if (b == NO_AREA) {
                areaInfo = (readString(readLong3()));
                IPAddrInfo.setProvider(readArea(offset + 8));
            } else {
                areaInfo = (readString(qqwryFile.getFilePointer() - 1));
                IPAddrInfo.setProvider(readArea(qqwryFile.getFilePointer()));
            }
            setAreaInfo(IPAddrInfo, areaInfo);
            return IPAddrInfo;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 从offset偏移开始解析后面的字节，读出一个地区名
     *
     * @param offset
     * @return 地区名字符串
     * @throws java.io.IOException
     */
    private String readArea(long offset) throws IOException {
        qqwryFile.seek(offset);
        byte b = qqwryFile.readByte();
        if (b == 0x01 || b == 0x02) {
            long areaOffset = readLong3(offset + 1);
            if (areaOffset == 0)
                return "未知地区";
            else
                return readString(areaOffset);
        } else
            return readString(offset);
    }

    /**
     * 从offset偏移处读取一个以0结束的字符串
     *
     * @param offset
     * @return 读取的字符串，出错返回空字符串
     */
    private String readString(long offset) {
        try {
            qqwryFile.seek(offset);
            int i;
            for (i = 0, buf[i] = qqwryFile.readByte(); buf[i] != 0; buf[++i] = qqwryFile.readByte())
                ;
            if (i != 0) {
                String country = IPEncode.bytesToString(buf, 0, i, "GBK");
                return country.equals(" CZ88.NET") ? "未知" : country;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "";
    }
}