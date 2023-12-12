package com.hanzaitu.common.certificate.verify;

public enum VerificationCodeEnum {

    USER_NOT_AUTHORIZED(4003,"未授权"),

    DATA_ERROR(5000,"数据传输错误");

    private Integer code;
    private String description;

    private VerificationCodeEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
