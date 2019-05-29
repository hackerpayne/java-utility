package com.lingdonge.http.webmagic.webmagic;

import com.lingdonge.http.webmagic.Request;
import com.kyle.utility.http.HttpConstant;
import org.testng.annotations.Test;

/**
 * @author code4crafter@gmail.com
 *         Date: 17/3/11
 */
public class RequestTest {

    @Test
    public void testEqualsAndHashCode() throws Exception {
        Request requestA = new Request("http://www.google.com/");
        Request requestB = new Request("http://www.google.com/");
//        assertThat(requestA.hashCode()).isEqualTo(requestB.hashCode());
//        assertThat(requestA).isEqualTo(requestB);
        requestA.setMethod(HttpConstant.Method.GET);
        requestA.setMethod(HttpConstant.Method.POST);
//        assertThat(requestA).isNotEqualTo(requestB);
//        assertThat(requestA.hashCode()).isNotEqualTo(requestB.hashCode());
    }
}
