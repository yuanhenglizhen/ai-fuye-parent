package com.hanzaitu.common.page;


import com.hanzaitu.common.core.HztBaseParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 分页查询请求参数
 **/
@ApiModel(description = "分页入参")
public class PageParam extends HztBaseParam implements Pageable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("当前页")
    protected Integer pageNo = 1;

    @ApiModelProperty("页大小")
    protected Integer pageSize = 10;

    @ApiModelProperty("是否分页")
    protected Boolean allowPaging = true;


    @Override
    public Integer getPageNo() {
        return pageNo == null ? 1 : this.pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    @Override
    public Integer getPageSize() {
        return pageSize == null ? 10 : this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isAllowPaging() {
        return allowPaging==null ? true:allowPaging.booleanValue();
    }

    public void setAllowPaging(boolean allowPaging) {
        this.allowPaging = allowPaging;
    }


}
