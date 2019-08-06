package com.lingdonge.core.page;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 分页工具类
 */
public class PageUtil {

    /**
     * 获取分页列表
     *
     * @param start
     * @param end
     * @return
     */
    public static List<Integer> getPageOfRange(Integer start, Integer end) {
        return IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }

    /**
     * 将页数和每页条目数转换为开始位置和结束位置<br>
     * 此方法用于不包括结束位置的分页方法<br>
     * 例如：<br>
     * 页码：1，每页10 =》 [0, 10]<br>
     * 页码：2，每页10 =》 [10, 20]<br>
     * 。。。<br>
     *
     * @param pageNo       页码（从1计数）
     * @param countPerPage 每页条目数
     * @return 第一个数为开始位置，第二个数为结束位置
     */
    public static int[] transToStartEnd(int pageNo, int countPerPage) {
        if (pageNo < 1) {
            pageNo = 1;
        }

        if (countPerPage < 1) {
            countPerPage = 0;
        }

        int start = (pageNo - 1) * countPerPage;
        int end = start + countPerPage;

        return new int[]{start, end};
    }

    /**
     * 根据总数计算总页数
     *
     * @param totalCount 总数
     * @param pageSize   每页数据量
     * @return 总页数
     */
    public static int totalPage(int totalCount, int pageSize) {
        if (pageSize == 0) {
            return 0;
        }
        return totalCount % pageSize == 0 ? (totalCount / pageSize) : (totalCount / pageSize + 1);
    }

}
