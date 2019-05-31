package com.lingdonge.core.util;

import org.junit.Test;

public class UtilsTest {

    @Test
    public void testPath() {
        System.out.println("<<<<<<<<<<< CLassPath >>>>>>>>>>>>>");
        System.out.println(Utils.ClassPath);

        System.out.println("<<<<<<<<<<< CurrentDir >>>>>>>>>>>>>");
        System.out.println(Utils.CurrentDir);

        System.out.println(this.getClass().getResource("/"));
    }

}
