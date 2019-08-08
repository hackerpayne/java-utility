package com.lingdonge.spider.jsoupxpath.model;


import com.lingdonge.spider.jsoupxpath.util.ScopeEm;

/**
 * xpath语法链的一个基本节点
 * @author github.com/zhegexiaohuozi [seimimaster@gmail.com]
 */
public class Node {
    private ScopeEm scopeEm;
    private String axis;
    private String tagName;
    private Predicate predicate;

    public ScopeEm getScopeEm() {
        return scopeEm;
    }

    public void setScopeEm(ScopeEm scopeEm) {
        this.scopeEm = scopeEm;
    }

    public String getAxis() {
        return axis;
    }

    public void setAxis(String axis) {
        this.axis = axis;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

}
