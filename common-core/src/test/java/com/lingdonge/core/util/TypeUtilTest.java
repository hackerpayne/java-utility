package com.lingdonge.core.util;


import com.lingdonge.core.reflect.Console;
import com.lingdonge.core.reflect.TypeUtil;
import org.testng.annotations.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TypeUtilTest {

    @Test
    public void getTypeTest() {

    }

    @Test
    public void getArgumentTypeTest() {
        List<String> list = new ArrayList<String>();
        list.add("aaaa");
        ParameterizedType type = (ParameterizedType) list.getClass().getGenericSuperclass();
        Type[] arguments = type.getActualTypeArguments();
        Console.log(arguments[0]);

        Type typeArgument = TypeUtil.getTypeArgument(list.getClass());
        Console.log(typeArgument);
    }
}
