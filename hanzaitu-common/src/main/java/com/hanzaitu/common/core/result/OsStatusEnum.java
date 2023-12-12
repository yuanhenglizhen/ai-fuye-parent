package com.hanzaitu.common.core.result;

/**
 * OS API返回结果状态枚举
 *
 * @author cr
 * @since 2023/05/27
 */
public enum OsStatusEnum implements IOsStatusEnum {
SUCCESS(10000, "成功"),
    FAIL(10001, "失败"),
    SYSTEM_ERROR(10002, "系统异常"),
    UNAUTHORIZED(10003, "未经认证,请先登录"),
    API_CALL_ERROR(10004, "API调用错误"),
    API_NONSUPPORT_ERROR(10005, "接口不支持"),
    RPC_CALL_ERROR(10006, "API调用错误"),
    SECOND_CONFIRMATION(10007, "需要二次确认"),
    FORBIDDEN(10010, "未经授权,请先授权"),

    PARAM_ERROR(10101, "参数错误"),
    DB_INSERT_ERROR(10103, "数据插入失败"),
    DB_UPDATE_ERROR(10104, "数据更新失败, 请稍候再试"),
    DB_DELETE_ERROR(10105, "数据删除失败，或已删除，请刷新页面"),
    PARAM_NOT_EMPTY(10106, "{}不能是空"),
    OPTION_ERROR(10107, "操作失败"),
    DATETIME_AFTER_ERROR(10108, "{}不得大于{}"),
    DATETIME_BEFORE_ERROR(10109, "{}必须大于{}"),
    TEMPORARY_SUPPORT(10110, "暂不支持该接口"),
    NOT_EMPTY(10111, "参数不能为空");

    OsStatusEnum(Integer status, String statusDesc) {
        this.status = status;
        this.statusDesc = statusDesc;
    }

    /** 状态标识 */
    private Integer status;

    /** 状态描述 */
    private String statusDesc;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    /**
     * 根据状态获取描述信息
     *
     * @param status
     * @return
     */
    public static String getDescByStatus(Integer status) {
        String statusDesc = "";
        if (status == null) {
            return statusDesc;
        }
        for (OsStatusEnum entry : OsStatusEnum.values()) {
            if (entry.getStatus().equals(status)) {
                statusDesc = entry.getStatusDesc();
                break;
            }
        }
        return statusDesc;
    }
}
