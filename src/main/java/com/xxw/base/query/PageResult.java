package com.xxw.base.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 分页实体对象
 * PostgreSQL 分页是每次取多少条，从第几条开始往后取多少条 eg：limit 10 offset 10 从第十条以后开始取十条数据出来
 * @author ivan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1874932588687379976L;

    public final static int DEFAULT_PAGE_SIZE = 20;

    public final static int PAGE_NUMBER = 1;

    /**
     * 当前第几页，参数传入
     **/
    private int pageNumber;

    /**
     * 每页的最大记录数，固定值，或者参数传入
     **/
    private int pageSize;

    /**
     * 总记录数(数据库查询)
     **/
    private int totalRecord;

    /**
     * 分页总数
     **/
    private int totalPage;

    /**
     * 分页数据(数据库查询)
     */
    private List<T> data;

    public PageResult(int pageSize, int totalRecord) {
        setPage(pageSize, totalRecord, null);
    }

    public PageResult(int pageSize, int totalRecord, List<T> data) {
       setPage(pageSize, totalRecord, data);
    }

    public PageResult(int pageSize, int totalRecord, List<T> data, int pageNumber) {
        setPage(pageSize, totalRecord, data);
        this.pageNumber = pageNumber;
    }

    private void setPage(int pageSize, int totalRecord, List<T> data){
        //获取总页数
        if (totalRecord / pageSize <= 0) {
            this.setTotalPage(1);
        } else {
            if (totalRecord % pageSize == 0) {
                this.setTotalPage(totalRecord / pageSize);
            } else {
                this.setTotalPage((totalRecord / pageSize) + 1);
            }
        }
        this.setPageSize(pageSize);
        this.setTotalRecord(totalRecord);
        this.setData(data);
    }

}
