package com.lamu.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by songliang on 2016/1/7.
 *
 * @author songliang
 */
public class Page<T> implements Serializable {
    List<T> data = new ArrayList<T>();
    private Integer currentPage;
    private Integer pageSize = 5;
    private Integer totalPage;
    private Integer total;
    private Integer toPage;

    public Integer getToPage() {
        return toPage;
    }

    public void setToPage(Integer toPage) {
        this.toPage = toPage;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
