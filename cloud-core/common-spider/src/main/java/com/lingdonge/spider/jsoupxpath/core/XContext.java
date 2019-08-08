package com.lingdonge.spider.jsoupxpath.core;


import com.lingdonge.spider.jsoupxpath.model.Node;

import java.util.LinkedList;

/**
 * @author github.com/zhegexiaohuozi [seimimaster@gmail.com]
 * @since 14-3-10
 */
public class XContext {
    public LinkedList<Node> xpathTr;
    public XContext(){
        if (xpathTr==null){
            xpathTr = new LinkedList<Node>();
        }
    }
}
