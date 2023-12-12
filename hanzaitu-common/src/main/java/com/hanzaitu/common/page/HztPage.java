package com.hanzaitu.common.page;

import cn.hutool.core.bean.BeanUtil;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页参数
 * Created by zhugc on 2018/9/25-13:40.
 */
@ToString
public class HztPage<S> implements Serializable{

    private static final long serialVersionUID = 1L;

    public HztPage(){}

    public HztPage(long pageNo, long pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    /**
     * 第几页
     */
    private long pageNo = 1;

    /**
     * 每页的条数
     */
    private long pageSize = 10;

    /**
     * 总条数
     */
    private long total;

    /**
     * 总页数
     */
    private long totalPages;

    /**
     * 是否是最后一页
     */
    private boolean isLastPage;

    private List<S> list = Collections.emptyList();

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total,long pageNo) {
        this.total = total;
        if(this.pageSize == 0){
            this.totalPages = 0 ;
        }
        this.totalPages = this.total / this.pageSize ;
        if(this.total % this.pageSize != 0){
            this.totalPages += 1 ;
        }
        if(totalPages > 0){
            this.setLastPage(pageNo == totalPages);
        }else{
            this.setLastPage(true);
        }
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public List<S> getList() {
        return this.list;
    }

    public void setList(List<S> list) {
        this.list = list;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isLastPage() {
        return this.isLastPage;
    }

    public void setLastPage(boolean lastPage) {
        isLastPage = lastPage;
    }

    public static <S> HztPage<S>  convert(List<S> list){
        int size = 10;
        int pageNo = 1;
        if(list != null && list.size()>0){
            size = list.size();
        }
        HztPage page = new HztPage(pageNo, size);
        page.setList(list);
        page.setLastPage(true);
        page.setTotal(list.size(), pageNo);
        page.setTotalPages(1);
        return page;
    }

    public <T> HztPage<T> convert(Class<T> clazz){
        HztPage<T> page = new HztPage(this.pageNo, this.pageSize);
        page.setTotal(this.getTotal(), this.getPageNo());
        page.setList(BeanUtil.copyToList(this.list, clazz));
        return page;
    }


    public static  <T> HztPage<T> emptyPage(PageParam pageParam){
        return new HztPage<T>(pageParam.getPageNo(), pageParam.getPageSize());
    }
}
