package com.lingdonge.spider.xsoup;

import org.jsoup.select.Elements;

import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public interface XElements {

    String get();

    List<String> list();

    public Elements getElements();

}
