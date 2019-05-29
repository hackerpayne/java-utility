package com.lingdonge.http.jsoupxpath.exception;

/**
 * 使用不存在的轴语法则抛出此异常
 * @author: github.com/zhegexiaohuozi [seimimaster@gmail.com]
 * Date: 14-3-15
 */
public class NoSuchAxisException extends Exception {
    public NoSuchAxisException(String msg){
        super(msg);
    }
}
