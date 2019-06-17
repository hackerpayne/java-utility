package com.lingdonge.spring.enums;

import cn.hutool.core.util.StrUtil;
import com.lingdonge.core.bean.base.NameValue;
import org.junit.Test;

import java.util.List;

public class EnvironmentEnumTest {

    @Test
    public void getAll() {
        List<NameValue> listAll = EnvironmentEnum.getItemList();
        listAll.forEach(item -> System.out.println(StrUtil.format("Key:{},Value:{}", item.getName(), item.getValue())));
    }

    @Test
    public void getItem() {
        System.out.println(EnvironmentEnum.getItem("stg"));
    }
}