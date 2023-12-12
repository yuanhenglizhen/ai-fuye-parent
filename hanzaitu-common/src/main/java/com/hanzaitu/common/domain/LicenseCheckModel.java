package com.hanzaitu.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @title LicenseCheckModel
 * @description 自定义需要校验的License参数
 * @author Administrator
 * @updateTime 2022/4/30 0030 18:19
 */
@Data
public class LicenseCheckModel implements Serializable {

    /**
     * 证书编号
     */
    private String licenseNo;

    /**
     * 可被允许的IP地址
     */
    private String ipAddress;

    /**
     * 可被允许的MAC地址
     */
    private String macAddress;

    /**
     * 可被允许的CPU序列号
     */
    private String cpuSerial;

    /**
     * 可被允许的主板序列号
     */
    private String mainBoardSerial;

    /**
     * 域名
     */
    private String domainName;

    /**
     * qq号码
     */
    private String qqNum;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 传输加密码
     */
    private String transportPwd;
}
