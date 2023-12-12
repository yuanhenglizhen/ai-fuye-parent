package com.hanzaitu.common.page;


import java.util.List;

public interface Pageable  {

     Integer getPageNo();

     Integer getPageSize();

     boolean isAllowPaging();

}
