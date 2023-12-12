package com.hanzaitu.common.config;

import com.hanzaitu.common.utils.file.FileUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 * 
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "hanzaitu")
@Data
public class HanZaiTuConfig
{
    /** 项目名称 */
    private String name;

    /** 版本 */
    private String version;

    /** 版权年份 */
    private String copyrightYear;

    /** 实例演示开关 */
    private boolean demoEnabled;

    /** 上传路径 */
    private static String profile;


    private static String staticDir;


    /** 获取地址开关 */
    private static boolean addressEnabled;

    /**
     * 短信验证码redis前缀
     */

    public static final String VERIFY_CODE_PREFIX = "verify_code_";

    /** 验证码类型 */
    private static String captchaType;

    private static Integer chatHistoryRetentionTime;


    public Integer getChatHistoryRetentionTime() {
        return chatHistoryRetentionTime;
    }

    public void setChatHistoryRetentionTime(Integer chatHistoryRetentionTime) {
        this.chatHistoryRetentionTime = chatHistoryRetentionTime;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getCopyrightYear()
    {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear)
    {
        this.copyrightYear = copyrightYear;
    }

    public boolean isDemoEnabled()
    {
        return demoEnabled;
    }

    public void setDemoEnabled(boolean demoEnabled)
    {
        this.demoEnabled = demoEnabled;
    }

    public static String getProfile()
    {
        return FileUtils.getAbsolutePath("");
    }

    public void setProfile(String profile)
    {
        HanZaiTuConfig.profile = profile;
    }

    public static boolean isAddressEnabled()
    {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled)
    {
        HanZaiTuConfig.addressEnabled = addressEnabled;
    }

    public static String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        HanZaiTuConfig.captchaType = captchaType;
    }

    /**
     * 获取导入上传路径
     */
    public static String getImportPath()
    {
        return getProfile() + "/import";
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }

    /**
     * 获取上传路径
     */
    public static String getBannerPath()
    {
        return getProfile() + "/banner";
    }
}
