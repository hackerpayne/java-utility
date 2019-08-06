package com.lingdonge.http.jsoupxpath.core;


import com.lingdonge.http.jsoupxpath.model.Node;

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
