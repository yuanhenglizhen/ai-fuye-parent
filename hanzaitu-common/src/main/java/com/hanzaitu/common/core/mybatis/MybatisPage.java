package com.hanzaitu.common.core.mybatis;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.page.Pageable;

import java.util.ArrayList;
import java.util.List;

public class MybatisPage<T> implements IPage<T> {

    /**
     * 排序字段
     */
    private List<OrderItem> orders = new ArrayList<>();

    /**
     * 分页记录
     */
    private List<T> records = new ArrayList<>();

    /**
     * 总条数
     */
    private long total ;

    /**
     * 每页条数
     */
    private long size;

    /**
     * 当前页
     */
    private long current;

    /**
     * 是否分页
     */
    protected boolean allowPaging = true;

    public void setOrders(List<OrderItem> orders) {
        this.orders = orders;
    }

    @Override
    public List<OrderItem> orders() {
        return orders;
    }

    @Override
    public List getRecords() {
        return records;
    }

    @Override
    public IPage setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public IPage setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public IPage setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public long getCurrent() {
        return current;
    }

    @Override
    public IPage setCurrent(long current) {
        this.current = current;
        return this;
    }

    public boolean isAllowPaging() {
        return allowPaging;
    }

    public void setAllowPaging(boolean allowPaging) {
        this.allowPaging = allowPaging;
    }

    /**
     * 转换为MybatisPage分页对象
     * @param pageParam
     * @return
     */
    public static MybatisPage convert(Pageable pageParam){
        MybatisPage mybatisPage = new MybatisPage();
        mybatisPage.setAllowPaging(pageParam.isAllowPaging());
        mybatisPage.setCurrent(pageParam.getPageNo());
        mybatisPage.setSize(pageParam.getPageSize());
        // 如果不分页，size强制置为-1
        if(!pageParam.isAllowPaging()){
            mybatisPage.setSize(-1);
        }
        return mybatisPage;
    }

    public HztPage<T> convert(){
        HztPage<T> page = new HztPage(this.current, this.size);
        if(!allowPaging){
            // 不分页
            page.setTotal(records.size(), this.current);
        }else {
            page.setTotal(this.getTotal(), this.current);
        }
        page.setList(this.records);
        return page;
    }

}
