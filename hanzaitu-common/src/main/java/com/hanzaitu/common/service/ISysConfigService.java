package com.hanzaitu.common.service;

import java.util.List;
import java.util.Map;

import com.hanzaitu.common.config.KD100Config;
import com.hanzaitu.common.domain.EmailConfig;
import com.hanzaitu.common.domain.HomeViewConfig;
import com.hanzaitu.common.domain.PayTypeConfig;
import com.hanzaitu.common.domain.SysConfig;
import com.hanzaitu.common.enums.ExpendTypeEnum;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.*;
import com.hanzaitu.common.vo.*;

/**
 * 参数配置 服务层
 * 
 * @author ruoyi
 */
public interface ISysConfigService
{
    /**
     * 查询参数配置信息
     * 
     * @param id 参数配置ID
     * @return 参数配置信息
     */
    public SysConfig selectConfigById(Long id);

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数键名
     * @return 参数键值
     */
    public String selectConfigByKey(String configKey);

    /**
     * 获取验证码开关
     * 
     * @return true开启，false关闭
     */
    public boolean selectCaptchaEnabled();

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int insertConfig(SysConfig config);

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int updateConfig(SysConfig config);

    /**
     * 批量删除参数信息
     * 
     * @param ids 需要删除的参数ID
     */
    public void deleteConfigByIds(Long[] ids);

    /**
     * 加载参数缓存数据
     */
    public void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    public void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    public void resetConfigCache();

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数信息
     * @return 结果
     */
    public boolean checkConfigKeyUnique(SysConfig config);

    /**
     * 获取联系我们配置
     * @return
     */
    ContactUsVO getContactUs();

    /**
     * 保存联系我们配置
     * @return
     */
    void saveContactUs(ContactUsParam param);

    /**
     * 获取微信支付配置
     * @return
     */
    WxPayConfigVO getWxPayConfig();

    /**
     * 保存微信支付配置
     * @return
     */
    void saveWxPayConfig(WxPayConfigParam param);

    /**
     * 获取支付宝支付配置
     * @return
     */
    AliPayConfigVO getAliPayConfig();

    /**
     * 保存支付宝支付配置
     * @return
     */
    void saveAliPayConfig(AliPayConfigParam param);

    /**
     * 获取支付宝支付配置
     * @return
     */
    YPayConfigVO getYPayConfig();

    /**
     * 保存支付宝支付配置
     * @return
     */
    void saveYPayConfig(YPayConfigParam param);


    /**
     * 获取支付宝支付配置
     * @return
     */
    AliPayAppConfigVO getAliPayAppConfig();

    /**
     * 保存支付宝支付配置
     * @return
     */
    void saveAliPayAppConfig(AliPayAppConfigParam param);

    /**
     * 获取支付积分配置
     * @return
     */
    List<PayToPointConfigVO> getPayToPointsConfig();

    /**
     * 获取支付积分配置
     * @return
     */
    void savePayToPointsConfig(List<PayToPointConfigParam> params);


    /**
     * 生成卡密
     * @param params
     */
    void createCipherCode(FinanceCipherCodeCreateParams params);

    /**
     * 生成卡密
     * @param params
     */
    HztPage<FinanceCipherCodeVO> listCipherCode(FinanceCipherCodeParam param);


    /**
     * 获取支付积分配置
     * @return
     */
    KD100Config getKD100Config();

    /**
     * 获取支付积分配置
     * @return
     */
    void saveKD100Config(KD100Config kd100Config);

    /**
     * 保存积分消耗配置
     * @return
     */
    void saveExpendConfig(List<ExpendConfigParam> params);

    /**
     * 获取积分消耗配置
     * @return
     */
    List<ExpendConfigParam> listExpendConfig();

    /**
     * 获取消耗配置
     * @return
     */
    List<Map<String, String>>  listExpendType();

    /**
     * 保存默认积分配置
     * @return
     */
    void saveDefaultPointsConfig(DefaultPointConfig params);

    /**
     * 获取默认积分配置
     * @return
     */
    DefaultPointConfig getDefaultPointsConfig();

    HomeViewConfig getHomeConfig();

    int saveHomeConfig(HomeViewConfig homeViewConfig);

    PayTypeConfig getPayTypeConfig();

    int savePayTypeConfig(PayTypeConfig payTypeConfig);


    EmailConfig getEmailConfig();

    int saveEmailConfig(EmailConfig EmailConfig);
}
