package com.xxw.base.query;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页实体对象
 * PostgreSQL 分页是每次取多少条，从第几条开始往后取多少条 eg：limit 10 offset 10 从第十条以后开始取十条数据出来
 * @author ivan
 */
@Data
@AllArgsConstructor
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 1874932588687379976L;

    public final static int PAGE_SIZE = 10;

    public final static int PAGE_NUMBER = 1;

    /**
     * 当前第几页，参数传入
     **/
    private int pageNumber = PAGE_NUMBER;

    /**
     * 每页的最大记录数，固定值，或者参数传入
     **/
    private int pageSize = PAGE_SIZE;

    /**
     * 总记录数(数据库查询)
     **/
    private int totalRecord;

    /**
     * 分页总数
     **/
    private int totalPage;

    /**
     * 开始的记录数
     **/
    private int startItems;

    /**
     * 结束的记录数
     **/
    private int endItems;

    /**
     * 分页数据(数据库查询)
     */
    private List<T> data;

    /**
     * 根据pageSize、pageNumber、totalRecord计算开始行、结束行和总页数
     **/
    public Page(int pageNumber, int pageSize, int totalRecord) {
        //当前查询的开始行
        this.startItems = pageSize * (pageNumber - 1) + 1;

        //当前查询的结束行
        this.endItems = pageSize * pageNumber;

        this.pageNumber = pageNumber;

        this.pageSize = pageSize;

        this.totalRecord = totalRecord;

        //获取总页数
        if (totalRecord / pageSize <= 0) {
            this.totalPage = 1;
        } else {
            if (totalRecord % pageSize == 0) {
                this.totalPage = totalRecord / pageSize;
            } else {
                this.totalPage = totalRecord / pageSize + 1;
            }
        }
    }


    /**
     * 根据pageSize、pageNumber、totalRecord计算开始行、结束行和总页数
     **/
    public Page(Page page) {
        //当前查询的开始行
        int firstItem = page.getPageSize() * (page.getPageNumber() - 1) + 1;

        //当前查询的结束行
        int lastItem = page.getPageSize() * page.getPageNumber();

        page.setStartItems(firstItem);
        page.setEndItems(lastItem);

        //获取总页数
        if (page.getTotalRecord() / page.getPageSize() <= 0) {
            page.setTotalPage(1);
        } else {
            if (page.getTotalRecord() % page.getPageSize() == 0) {
                page.setTotalPage(page.getTotalRecord() / page.getPageSize());
            } else {
                page.setTotalPage((page.getTotalRecord() / page.getPageSize()) + 1);
            }
        }
    }

    public Page() {

    }

    /**
     * 根据pageSize、pageNumber、totalRecord计算开始行、结束行和总页数
     **/
    public Page(int pageNumber, int pageSize) {
        //当前查询的开始行
        this.startItems = pageSize * (pageNumber - 1);

        //当前查询的结束行
        this.endItems = pageSize;

        this.pageNumber = pageNumber;

        this.pageSize = pageSize;
    }

}
