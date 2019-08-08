package com.lingdonge.spider.jsoupxpath.util;


import org.apache.commons.lang3.StringUtils;

/**
 * 操作符
 * @author github.com/zhegexiaohuozi [seimimaster@gmail.com]
 * @since 14-3-7
 */
public enum OpEm {
    PLUS("+"){
        @Override
        public Object excute(String left,String right) {
            int li = 0;
            if (StringUtils.isNotBlank(left)){
                li = Integer.parseInt(left);
            }
            int ri = 0;
            if (StringUtils.isNotBlank(right)){
                ri=Integer.parseInt(right);
            }
            return li + ri;
        }
    },
    MINUS("-"){
        @Override
        public Object excute(String left, String right) {
            int li = 0;
            if (StringUtils.isNotBlank(left)){
                li = Integer.parseInt(left);
            }
            int ri = 0;
            if (StringUtils.isNotBlank(right)){
                ri=Integer.parseInt(right);
            }
            return li - ri;
        }
    },
    EQ("="){
        @Override
        public Object excute(String left, String right) {
            return left.equals(right);
        }
    },
    NE("!="){
        @Override
        public Object excute(String left, String right) {
            return !left.equals(right);
        }
    },
    GT(">"){
        @Override
        public Object excute(String left, String right) {
            int li = 0;
            if (StringUtils.isNotBlank(left)){
                li = Integer.parseInt(left);
            }
            int ri = 0;
            if (StringUtils.isNotBlank(right)){
                ri=Integer.parseInt(right);
            }
            return li > ri;
        }
    },
    LT("<"){
        @Override
        public Object excute(String left, String right) {
            int li = 0;
            if (StringUtils.isNotBlank(left)){
                li = Integer.parseInt(left);
            }
            int ri = 0;
            if (StringUtils.isNotBlank(right)){
                ri=Integer.parseInt(right);
            }
            return li < ri;
        }
    },
    GE(">="){
        @Override
        public Object excute(String left, String right) {
            int li = 0;
            if (StringUtils.isNotBlank(left)){
                li = Integer.parseInt(left);
            }
            int ri = 0;
            if (StringUtils.isNotBlank(right)){
                ri=Integer.parseInt(right);
            }
            return li >= ri;
        }
    },
    LE("<="){
        @Override
        public Object excute(String left, String right) {
            int li = 0;
            if (StringUtils.isNotBlank(left)){
                li = Integer.parseInt(left);
            }
            int ri = 0;
            if (StringUtils.isNotBlank(right)){
                ri=Integer.parseInt(right);
            }
            return li <= ri;
        }
    },
    STARTWITH("^="){
        @Override
        public Object excute(String left, String right) {
            return left.startsWith(right);
        }
    },
    ENDWITH("$="){
        @Override
        public Object excute(String left, String right) {
            return left.endsWith(right);
        }
    },
    CONTAIN("*="){
        @Override
        public Object excute(String left, String right) {
            return left.contains(right);
        }
    },
    REGEX("~="){
        @Override
        public Object excute(String left, String right) {
            return left.matches(right);
        }
    },
    NOTMATCH("!~"){
        @Override
        public Object excute(String left, String right) {
            return !left.matches(right);
        }
    };
    private String val;
    private OpEm(String type){
        this.val = type;
    }
    public String val() {
        return this.val;
    }

    public Object excute(String left,String right){
        return null;
    }
}
