package com.lingdonge.db.util;

import cn.hutool.core.util.StrUtil;
import com.lingdonge.core.bean.base.BaseEntity;

import java.util.List;

/**
 * 分页工具类
 * 只有3项能设置：
 * totalCount 总记录数
 * pageSize 每页显示的记录数
 * pageCurrrent 当前页码
 * <p>
 * 此类用于分页时显示多项内容，计算页码和当前页的偏移量。
 * </p>
 * <p>
 * <p>
 * 该类只需要知道总的数据项数，当前显示第几页，每页显示几项，就可以帮你计算出其它数据，而且保证所有计算都得出合理的值，不用担心页码超出边界之类的问题。
 * </p>
 * <p>
 * <p>
 * 使用方法如下:
 * <pre><![CDATA[
 *
 *   // 创建一个分页器，可以在此指定每页显示几项，也可以在后续指定。
 *   // 如果没有指定，则使用默认值每页最多显示10条。
 *   Pager pt = new Pager();        // 或 new Pager(itemsPerPage);
 *
 *   // 告诉我总共有几项。如果给的数字小于0，就看作0。
 *   pg.setItemsTotal(123);
 *
 *   // 如果不知道有几项，可以这样。
 *   pg.setItemsTotal(Pager.UNKNOWN_ITEMS);
 *
 *   // 现在默认当前页是第一页，但你可以改变它。
 *   pg.setCurPage(3);                         // 这样当前页就是3了，不用担心页数会超过总页数。
 *
 *   // 现在你可以得到各种数据了。
 *   int currentPage = pg.getCurPage();        // 得到当前页
 *   int totalPages  = pg.getPages();       // 总共有几页
 *   int totalItems  = pg.getItemsTotal();       // 总共有几项
 *   int beginIndex  = pg.getBeginIndex();  // 得到当前页第一项的序号(从1开始数的)
 *   int endIndex    = pg.getEndIndex();    // 得到当前页最后一项的序号(也是从1开始数)
 *   int offset      = pg.getOffset();      // offset和length可以作为mysql查询语句
 *   int length      = pg.getActualLength();      //    实际的条数
 *
 *   // 还可以做调整。
 *   setItemsPerPage(20);                   // 这样就每页显示20个了，当前页的值会自动调整,
 *                                          //     使新页和原来的页显示同样的项，这样用户不容易迷惑。
 *   setItemsTotal(33);                           // 这样可以把页码调整到显示第33号项(从0开始计数)的那一页
 *
 *   // 高级功能，开一个滑动窗口。我们经常要在web页面上显示一串的相邻页码，供用户选择。
 *   //        ____________________________________________________________
 *   // 例如:  <<     <       3     4    5    6    [7]    8    9    >    >>
 *   //        ^      ^                             ^               ^    ^
 *   //       第一页 前一页                       当前页          后一页 最后一页
 *   //
 *   // 以上例子就是一个大小为7的滑动窗口，当前页码被尽可能摆在中间，除非当前页位于开头或结尾。
 *   // 使用下面的调用，就可以得到指定大小的滑动窗口中的页码数组。
 *   int[] slider = pg.getSlider(7);
 *
 *   // 这样可以判断指定页码是否有效，或者是当前页。无效的页码在web页面上不需要链接。
 *   if (pg.isDisabledPage(slider[i])) {
 *       show = "page " + slider[i];
 *   } else {
 *       show = "<a href=#> page " + slider[i] + " </a>";
 *   }
 *
 *   // 可以直接打印出pg，用于调试程序。
 *   System.out.println(pg);
 *   log.debug(pg);
 *
 * ]]></pre>
 * </p>
 */
public class Pager extends BaseEntity {

    /**
     * 当前页码
     */
    private int pageCurrent = 1;
    /**
     * 每页记录数
     */
    private int pageSize = 10;
    /**
     * 总页数
     */
    private int pageCount;
    /**
     * 总记录数
     */
    private int totalCount;


    private Integer startIndex = 0;

    private Integer endIndex;

    public Integer getStartIndex() {
        return startIndex;
    }

    public Integer getEndIndex() {
        return endIndex;
    }

    /**
     * 分页的列表数据
     */
    private List<?> list;

    /**
     * 创建一个分页器，默认每页显示<code>10</code>项。
     */
    public Pager() {
    }

    public Pager(Integer totalCount) {
        this.totalCount = totalCount;
        caculatePage();
    }

    public Pager(Integer totalCount, Integer pageSize) {
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        caculatePage();
    }

    /**
     * 设置当前页码
     *
     * @param pageCurrent
     */
    public void setPageCurrent(int pageCurrent) {
        this.pageCurrent = pageCurrent;
        caculatePage();
    }

    /**
     * 获取当前页数
     *
     * @return
     */
    public int getPageCurrent() {
        return pageCurrent;
    }

    /**
     * 获取每页显示的数据量
     *
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置并取得每页项数。如果指定的每页项数小于等于0，则使用默认值<code>pageSize</code>。 并调整当前页使之在改变每页项数前后显示相同的项。
     *
     * @param pageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = (pageSize > 0) ? pageSize : this.pageSize;
        caculatePage();
    }

    /**
     * 获取页面数量，一共有多少页
     *
     * @return
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * 获取记录数量
     *
     * @return
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 设置并取得总项数。如果指定的总项数小于0，则被看作0。自动调整当前页，确保当前页值在正确的范围内。
     *
     * @param totalCount
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = (totalCount >= 0) ? totalCount : 0;
        caculatePage();
    }

    /**
     * 设置第几条记录，使之返回该条记录所在的页数据项
     * <br>
     * 如每页显示10条，设置25，将返回第25条记录所在页的数据项(21-30)
     *
     * @param offset
     * @return
     */
    public int setOffset(Integer offset) {
        setPageCurrent((offset / pageSize) + 1);
        caculatePage();
        return pageCurrent;
    }

    /**
     * 取得当前页。
     *
     * @return 当前页
     */
    public int getCurPage() {
        return pageCurrent;
    }

    /**
     * 计算数据，不管是改变页数，还是设置页码之后，统一进行计算
     */
    public void caculatePage() {

        // 计数总页数
        if (totalCount % pageSize == 0) {
            this.pageCount = totalCount / pageSize;
        } else {
            this.pageCount = (totalCount / pageSize) + 1;
        }

//        return (int) Math.ceil((double) totalCount / pageSize);

        // 计算新的起始位置
        this.startIndex = (pageCurrent > 0) ? (pageSize * (pageCurrent - 1)) : 0;

        // 计算新的结束位置
        Integer lastIndex = 0;
        if (totalCount < pageSize) {
            lastIndex = totalCount;
        } else if ((totalCount % pageSize == 0) || (totalCount % pageSize != 0 && pageCurrent < pageCount)) {
            lastIndex = pageCurrent * pageSize;
        } else if (totalCount % pageSize != 0 && pageCurrent == pageCount) {//最后一页
            lastIndex = totalCount;
        }
        this.endIndex = lastIndex;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    /**
     * 取得当前页的长度，即当前页的实际项数。相当于 <code>endIndex() - beginIndex() + 1</code>
     *
     * @return 当前页的长度
     */
    public int getActualLength() {
        if (pageCurrent > 0) {
            return Math.min(pageSize * pageCurrent, totalCount) - (pageSize * (pageCurrent - 1));
        } else {
            return 0;
        }
    }

    /**
     * 取得首页页码。
     *
     * @return 首页页码
     */
    public int getFirstPage() {
        return calcPage(1);
    }

    /**
     * 取得末页页码。
     *
     * @return 末页页码
     */
    public int getLastPage() {
        return calcPage(pageCount);
    }

    /**
     * 取得前一页页码。
     *
     * @return 前一页页码
     */
    public int getPreviousPage() {
        return calcPage(pageCurrent - 1);
    }

    /**
     * 取得前n页页码
     *
     * @param n 前n页
     * @return 前n页页码
     */
    public int getPreviousPage(int n) {
        return calcPage(pageCurrent - n);
    }

    /**
     * 取得后一页页码。
     *
     * @return 后一页页码
     */
    public int getNextPage() {
        return calcPage(pageCurrent + 1);
    }

    /**
     * 取得后n页页码。
     *
     * @param n 后n面
     * @return 后n页页码
     */
    public int getNextPage(int n) {
        return calcPage(pageCurrent + n);
    }

    /**
     * 判断指定页码是否被禁止，也就是说指定页码超出了范围或等于当前页码。
     *
     * @param page 页码
     * @return boolean  是否为禁止的页码
     */
    public boolean isDisabledPage(int page) {
        return ((page < 1) || (page > pageCount) || (page == this.pageCurrent));
    }

    /**
     * 取得默认大小(<code>DEFAULT_SLIDER_SIZE</code>)的页码滑动窗口，并将当前页尽可能地放在滑动窗口的中间部位。参见{@link #getSlider(int
     * n)}。
     *
     * @return 包含页码的数组
     */
    public int[] getSlider() {
        return getSlider(pageSize);
    }

    /**
     * 取得指定大小的页码滑动窗口，并将当前页尽可能地放在滑动窗口的中间部位。例如: 总共有13页，当前页是第5页，取得一个大小为5的滑动窗口，将包括 3，4，5，6,
     * 7这几个页码，第5页被放在中间。如果当前页是12，则返回页码为 9，10，11，12，13。
     *
     * @param pWidth 滑动窗口大小
     * @return 包含页码的数组，如果指定滑动窗口大小小于1或总页数为0，则返回空数组。
     */
    public int[] getSlider(int pWidth) {
        int width = pWidth;
        int pages = pageCount;

        if ((pages < 1) || (width < 1)) {
            return new int[0];
        } else {
            if (width > pages) {
                width = pages;
            }

            int[] slider = new int[width];
            int first = pageCurrent - ((width - 1) / 2);

            if (first < 1) {
                first = 1;
            }

            if (((first + width) - 1) > pages) {
                first = pages - width + 1;
            }

            for (int i = 0; i < width; i++) {
                slider[i] = first + i;
            }

            return slider;
        }
    }

    /**
     * 计算页数，但不改变当前页。
     *
     * @param page 页码
     * @return 返回正确的页码(保证不会出边界)
     */
    protected int calcPage(int page) {
        int pages = pageCount;
        if (pages > 0) {
            return (page < 1) ? 1 : ((page > pages) ? pages : page);
        }

        return 0;
    }

    /**
     * 转换成字符串表示。
     *
     * @return 字符串表示。
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Pager: page ");

        if (pageCount < 1) {
            sb.append(getCurPage());
        } else {
            int[] slider = getSlider();

            for (int i = 0; i < slider.length; i++) {
                if (isDisabledPage(slider[i])) {
                    sb.append('[').append(slider[i]).append(']');
                } else {
                    sb.append(slider[i]);
                }

                if (i < (slider.length - 1)) {
                    sb.append('\t');
                }
            }
        }

        sb.append(" of ").append(pageCount).append(",\n");
        sb.append(StrUtil.format("Total Count:[{}],Total Page:[{}];", totalCount, pageCount));
        sb.append(StrUtil.format("Showing Items From:[{}]-[{}];", startIndex, endIndex));
        sb.append(StrUtil.format("Offset:[{}],Length:[{}];", startIndex, getActualLength()));
        sb.append(StrUtil.format("Next Page :[{}]，Pre Page:[{}]", getNextPage(), getPreviousPage()));

        return sb.toString();
    }

    public static void main(String[] args) {

        Pager pager = new Pager();
        pager.setTotalCount(1002);
        pager.setPageSize(10);
        System.out.println(pager);

        // 测滑动窗口
        pager.setPageCurrent(36);
        System.out.println(pager);

        // 改页码
        pager.setPageCurrent(2);
        System.out.println(pager);

        // 改页面大小
        pager.setPageSize(100);
        System.out.println(pager);

        // 改总数
        pager.setTotalCount(109);
        System.out.println(pager);

    }
}

