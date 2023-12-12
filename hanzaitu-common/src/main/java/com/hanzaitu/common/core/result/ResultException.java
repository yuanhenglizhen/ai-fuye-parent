package com.hanzaitu.common.core.result;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import com.alibaba.fastjson.JSON;

/**
 *
 * @author xiangtao
 * @date 2019/12/16
 */
public class ResultException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer status;
    private final String msg;
    private String description = "";
    private IOsStatusEnum em;
    private Object[] params;

    public static ResultException createResultException(String msg, Object... params) {
        if (params != null && params.length > 0) {
            msg = ParameterizedMessageFactory.INSTANCE.newMessage(msg, params).getFormattedMessage();
        }
        return new ResultException(msg, params);
    }

    public static ResultException createResultException(IOsStatusEnum em, Object... OsStatusEnumParams) {
        return new ResultException(em, OsStatusEnumParams);
    }

    public static ResultException createResultException(Throwable cause){
        return new ResultException(cause);
    }

    private ResultException(String msg, Object... params) {
        super(msg);
        this.status = OsStatusEnum.FAIL.getStatus();
        this.msg = msg;
        this.params = params;
    }

    private ResultException(Throwable cause){
        super(cause);
        this.status = OsStatusEnum.FAIL.getStatus();
        this.msg = cause.getMessage();
    }

    public ResultException(IOsStatusEnum em, Object... OsStatusEnumParams) {
        super((OsStatusEnumParams != null && OsStatusEnumParams.length > 0)
            ? ParameterizedMessageFactory.INSTANCE.newMessage(em.getStatusDesc(), OsStatusEnumParams).getFormattedMessage()
            : em.getStatusDesc());
        this.status = em.getStatus();
        this.msg = this.getMessage();
        this.em = em;
        this.params = OsStatusEnumParams;
    }

    public Integer getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public String getDescription() {
        return description;
    }

    public IOsStatusEnum getEm() {
        return em;
    }

    public Object[] getParams() {
        return params;
    }

    public ResultException log(Logger log, Object param) {
        log.error(this.msg + "；参数：{}", JSON.toJSONString(param));
        return this;
    }

    public ResultException log(Logger log, String text, Object... params) {
        log.error(text, params);
        return this;
    }

}