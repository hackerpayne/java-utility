package com.lingdonge.core.encode;

import org.testng.annotations.Test;

public class Base62Test {


    @Test
    public void test() {

        String encode = Base62.encodeBase62("12345");
        System.out.println("加密结果为：" + encode);

        String decode = Base62.decodeBase62Str(encode);
        System.out.println("解密结果为：" + decode);
    }

}
