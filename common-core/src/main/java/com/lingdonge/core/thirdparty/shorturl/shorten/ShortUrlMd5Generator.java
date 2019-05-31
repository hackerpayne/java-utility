package com.lingdonge.core.thirdparty.shorturl.shorten;

import com.lingdonge.core.thirdparty.shorturl.ShortUrlUtil;
import com.lingdonge.core.thirdparty.shorturl.ShortenService;

/**
 * Java版短网址(ShortUrl)的算法
 * 实现：
 * 1、将长网址用md5算法生成32位签名串，分为4段，每段8个字符。
 * 2、对这4段循环处理，取每段的8个字符, 将他看成16进制字符串与0x3fffffff(30位1)的位与操作，超过30位的忽略处理。多了也没用因为下面要分成6段  嘿嘿正好取整。注意用Long型变量（长度问题  你懂得）
 * 3、将每段得到的30位字符（后台以long十进制显示）又分成6段，通过移位运算将每5位分别与字符数组求与运算（0x0000003D），得到其在字符数组中的索引并取出拼串。
 * 4、这样一个md5字符串可以获得4个6位串，取里面的任意一个就可作为这个长url的短url地址。这种算法,虽然会生成4个,但是仍然存在重复几率
 * 跳转原理：当我们生成短链接之后，只需要在表中（数据库或者NoSql ）存储原始链接与短链接的映射关系即可。当我们访问短链接时，只需要从映射关系中找到原始链接，即可跳转到原始链接。
 * eg: http://6du.in/0W13as
 * 优点：存在碰撞（重复）的可能性，虽然几率很小。短码位数是比较固定的。不会从一位长度递增到多位的。据说微博使用的这种算法。
 */
public class ShortUrlMd5Generator implements ShortenService {

    /**
     * 默认使用5位长度转换短址
     *
     * @param longUrl
     * @return
     */
    @Override
    public String shorten(String longUrl) {
        return ShortUrlUtil.shorten(longUrl, 5);
    }

    @Override
    public String shorten(String longUrl, Integer shortLength) {
        return ShortUrlUtil.shorten(longUrl, 6);
    }

}