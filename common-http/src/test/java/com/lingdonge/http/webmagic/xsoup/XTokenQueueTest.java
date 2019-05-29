package com.lingdonge.http.webmagic.xsoup;

import com.lingdonge.http.xsoup.XTokenQueue;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author code4crafter@gmail.com
 */
public class XTokenQueueTest {

    @Test
    public void testParseFunctionParams() {
        List<String> list = XTokenQueue.parseFuncionParams("a,b,c");
//        assertThat(list).hasSize(3);

        list = XTokenQueue.parseFuncionParams("'a,b',c");
//        assertThat(list).hasSize(2);

        list = XTokenQueue.parseFuncionParams("'a,\\'b',c");
//        assertThat(list).hasSize(2);

        list = XTokenQueue.parseFuncionParams("@a,1,c");
//        assertThat(list).hasSize(3);

    }

    @Test
    public void testChompBalancedQuotes() throws Exception {
        XTokenQueue xTokenQueue = new XTokenQueue("\"aaaaa\"");
        String chomp = xTokenQueue.chompBalancedQuotes();
//        assertThat(chomp).isEqualTo("\"aaaaa\"");

        xTokenQueue = new XTokenQueue("\"aaaaa\"aabb");
        chomp = xTokenQueue.chompBalancedQuotes();
//        assertThat(chomp).isEqualTo("\"aaaaa\"");

        xTokenQueue = new XTokenQueue("a\"aaaaa\"aabb");
        chomp = xTokenQueue.chompBalancedQuotes();
//        assertThat(chomp).isEqualTo("");

    }

    @Test
    public void testChompBalancedInQuotes() throws Exception {
        XTokenQueue xTokenQueue = new XTokenQueue("(\")\")");
        String chomp = xTokenQueue.chompBalancedNotInQuotes('(', ')');
//        assertThat(chomp).isEqualTo("\")\"");

        xTokenQueue = new XTokenQueue("(\"')\")");
        chomp = xTokenQueue.chompBalancedNotInQuotes('(', ')');
//        assertThat(chomp).isEqualTo("\"')\"");

        xTokenQueue = new XTokenQueue("(''')')");
        chomp = xTokenQueue.chompBalancedNotInQuotes('(', ')');
//        assertThat(chomp).isEqualTo("''')'");

    }
}
