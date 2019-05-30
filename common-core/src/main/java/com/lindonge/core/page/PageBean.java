package com.lindonge.core.page;

import com.lindonge.core.bean.base.BaseEntity;

import java.util.List;

/**
 * 分页对象。用于封装分页相关的数据
 */
//public PageBean findPageBean(int curPage,int pageSize) {
//        // 1.创建PageBean对象
//        PageBean<Employee> pageBean = new PageBean<Employee>();
//        // 2.设置当前页数据
//        pageBean.setCurPage(curPage);
//        pageBean.setPageSize(pageSize);
//        // 3. 获取总记录数
//        pageBean.setTotalCount(empDao.queryCount());
//        // 4.获取当前页数据
//        List<Employee> emps = empDao.queryData(pageBean.getCurPage(),pageBean.getPageSize());
//        pageBean.setData(emps);
//        return pageBean;
//        }
public class PageBean<T> extends BaseEntity {

    /**
     * 当前页数据(查询数据库得到)
     */
    private List<T> data;

    /**
     * 首页
     */
    private int firstPage;

    /**
     * 上一页
     */
    private int prePage;

    /**
     * 下一页
     */
    private int nextPage;

    /**
     * 末页/总页数
     */
    private int totalPage;

    /**
     * 当前页
     */
    private int curPage;

    /**
     * 总记录数(查询数据库得到)
     */
    private int totalCount;

    /**
     * 每页记录数，默认10
     */
    private int pageSize = 10;

    public PageBean() {

    }

    public PageBean(Integer pageNumber) {
        this.curPage = pageNumber;
    }

    /**
     * 传入当前页和当前每页的数量
     *
     * @param pageNumber
     * @param pageSize
     */
    public PageBean(Integer pageNumber, Integer pageSize) {
        this.curPage = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * 构造函数，默认生成指定的数量
     *
     * @param listData
     * @param pageNumber
     * @param pageSize
     * @param totalCount
     */
    public PageBean(List<T> listData, Integer pageNumber, Integer pageSize, Integer totalCount) {
        this.data = listData;
        this.curPage = pageNumber;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
    }

    public List<T> getData() {
        return data;
    }

    public PageBean setData(List<T> data) {
        this.data = data;
        return this;
    }

    //首页
    public int getFirstPage() {
        return 1;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    /**
     * 上一页： 算法：如果当前页是首页，则为1，否则为当前页-1
     *
     * @return
     */
    public int getPrePage() {
        return this.getCurPage() == this.getFirstPage() ? 1 : this.getCurPage() - 1;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    /**
     * 下一页：算法：如果当前页是末页，则为末页，否则当前页+1
     *
     * @return
     */
    public int getNextPage() {
        return this.getCurPage() == this.getTotalPage() ? this.getTotalPage() : this.getCurPage() + 1;
    }

    public PageBean setNextPage(int nextPage) {
        this.nextPage = nextPage;
        return this;
    }

    /**
     * 总页数、末页： 算法：如果总记录数%每页记录数==0 ？ 总记录数/每页记录数 ：总记录数/每页记录数+1
     *
     * @return
     */
    public int getTotalPage() {
        return this.getTotalCount() % this.getPageSize() == 0 ? this.getTotalCount() / this.getPageSize() : this.getTotalCount() / this.getPageSize() + 1;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getCurPage() {
        return curPage;
    }

    public PageBean setCurPage(int curPage) {
        this.curPage = curPage;
        return this;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public PageBean setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    /**
     * 每页记录数
     *
     * @return
     */
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}