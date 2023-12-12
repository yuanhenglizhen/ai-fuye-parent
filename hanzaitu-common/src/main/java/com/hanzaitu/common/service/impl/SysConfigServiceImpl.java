package com.hanzaitu.common.service.impl;

import java.util.*;
import javax.annotation.PostConstruct;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.hanzaitu.common.config.KD100Config;
import com.hanzaitu.common.constant.Constants;
import com.hanzaitu.common.core.HztBaseService;
import com.hanzaitu.common.domain.*;
import com.hanzaitu.common.enums.CipherStatus;
import com.hanzaitu.common.enums.ExpendTypeEnum;
import com.hanzaitu.common.enums.SysConfigKeyEnum;
import com.hanzaitu.common.page.HztPage;
import com.hanzaitu.common.params.*;
import com.hanzaitu.common.service.FinanceCipherCodeService;
import com.hanzaitu.common.utils.JacksonUtil;
import com.hanzaitu.common.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hanzaitu.common.annotation.DataSource;
import com.hanzaitu.common.constant.CacheConstants;
import com.hanzaitu.common.constant.UserConstants;
import com.hanzaitu.common.core.redis.RedisCache;
import com.hanzaitu.common.core.text.Convert;
import com.hanzaitu.common.enums.DataSourceType;
import com.hanzaitu.common.exception.ServiceException;
import com.hanzaitu.common.utils.StringUtils;
import com.hanzaitu.common.mapper.SysConfigMapper;
import com.hanzaitu.common.service.ISysConfigService;

import static com.hanzaitu.common.constant.CacheConstants.CHAT_EXPIRED_CONFIG_KEY;

/**
 * 参数配置 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl extends HztBaseService implements ISysConfigService
{
    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private FinanceCipherCodeService financeCipherCodeService;
    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {
        loadingConfigCache();
    }

    /**
     * 查询参数配置信息
     * 
     * @param id 参数配置ID
     * @return 参数配置信息
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public SysConfig selectConfigById(Long id)
    {
        SysConfig config = new SysConfig();
        config.setConfigId(id);
        return configMapper.selectConfig(config);
    }

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = configMapper.selectConfig(config);
        if (StringUtils.isNotNull(retConfig))
        {
            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     * 
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled()
    {
        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled))
        {
            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config)
    {
        return configMapper.selectConfigList(config);
    }

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config)
    {
        int row = configMapper.insertConfig(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config)
    {
        SysConfig temp = configMapper.selectConfigById(config.getId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey()))
        {
            redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        int row = configMapper.updateConfig(config);
        if (row > 0)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     * 
     * @param ids 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] ids)
    {
        for (Long id : ids)
        {
            SysConfig config = selectConfigById(id);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            configMapper.deleteConfigById(id);
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache()
    {
        List<SysConfig> configsList = configMapper.selectConfigList(new SysConfig());
        for (SysConfig config : configsList)
        {
            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache()
    {
        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache()
    {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config)
    {
        Long id = StringUtils.isNull(config.getId()) ? -1L : config.getId();
        SysConfig info = configMapper.checkConfigKeyUnique(config.getConfigKey());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != id.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public ContactUsVO getContactUs() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.CONTACT_US.getValue()).one();
        if (Objects.isNull(config)) {
            return new ContactUsVO();
        }
        ContactUsVO contactUsVO = JSON.parseObject(config.getConfigValue(), ContactUsVO.class);
        return contactUsVO;
    }

    @Override
    public void saveContactUs(ContactUsParam param) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.CONTACT_US.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.CONTACT_US.getValue());
            config.setConfigName(SysConfigKeyEnum.CONTACT_US.getDescription());
        }
        config.setConfigValue(JSONUtil.toJsonStr(param));
        configMapper.saveOrUpdateById(config);
    }

    @Override
    public WxPayConfigVO getWxPayConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.WX_PAY.getValue()).one();
        if (Objects.isNull(config)) {
            return new WxPayConfigVO();
        }
        WxPayConfigVO wxPayConfigVO = JSON.parseObject(config.getConfigValue(), WxPayConfigVO.class);
        return wxPayConfigVO;
    }

    @Override
    public void saveWxPayConfig(WxPayConfigParam param) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.WX_PAY.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.WX_PAY.getValue());
            config.setConfigName(SysConfigKeyEnum.WX_PAY.getDescription());
        }
        config.setConfigValue(JSONUtil.toJsonStr(param));
        configMapper.saveOrUpdateById(config);
    }

    @Override
    public AliPayConfigVO getAliPayConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.ALI_PAY.getValue()).one();
        if (Objects.isNull(config)) {
            return new AliPayConfigVO();
        }
        AliPayConfigVO aliPayConfigVO = JSON.parseObject(config.getConfigValue(), AliPayConfigVO.class);
        return aliPayConfigVO;
    }

    @Override
    public void saveAliPayConfig(AliPayConfigParam param) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.ALI_PAY.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.ALI_PAY.getValue());
            config.setConfigName(SysConfigKeyEnum.ALI_PAY.getDescription());
        }
        config.setConfigValue(JSONUtil.toJsonStr(param));
        configMapper.saveOrUpdateById(config);
    }

    @Override
    public YPayConfigVO getYPayConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.Y_PAY.getValue()).one();
        if (Objects.isNull(config)) {
            return new YPayConfigVO();
        }
        YPayConfigVO yPayConfigVO = JSON.parseObject(config.getConfigValue(), YPayConfigVO.class);
        return yPayConfigVO;
    }

    @Override
    public void saveYPayConfig(YPayConfigParam param) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.Y_PAY.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.Y_PAY.getValue());
            config.setConfigName(SysConfigKeyEnum.Y_PAY.getDescription());
        }
        config.setConfigValue(JSONUtil.toJsonStr(param));
        configMapper.saveOrUpdateById(config);
    }

    @Override
    public AliPayAppConfigVO getAliPayAppConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.ALI_PAY_APP.getValue()).one();
        if (Objects.isNull(config)) {
            return new AliPayAppConfigVO();
        }
        AliPayAppConfigVO aliPayAppConfigVO = JSON.parseObject(config.getConfigValue(), AliPayAppConfigVO.class);
        return aliPayAppConfigVO;
    }

    @Override
    public void saveAliPayAppConfig(AliPayAppConfigParam param) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.ALI_PAY_APP.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.ALI_PAY_APP.getValue());
            config.setConfigName(SysConfigKeyEnum.ALI_PAY_APP.getDescription());
        }
        config.setConfigValue(JSONUtil.toJsonStr(param));
        configMapper.saveOrUpdateById(config);
    }

    @Override
    public List<PayToPointConfigVO> getPayToPointsConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.PAY_TO_POINTS.getValue()).one();
        if (Objects.isNull(config)) {
            return Collections.EMPTY_LIST;
        }
        List<PayToPointConfigVO> payToPointConfigVOS = JSON.parseArray(config.getConfigValue(), PayToPointConfigVO.class);
        return payToPointConfigVOS;
    }

    @Override
    public void savePayToPointsConfig(List<PayToPointConfigParam> params) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.PAY_TO_POINTS.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.PAY_TO_POINTS.getValue());
            config.setConfigName(SysConfigKeyEnum.PAY_TO_POINTS.getDescription());
        }
        config.setConfigValue(JSONUtil.toJsonStr(params));
        configMapper.saveOrUpdateById(config);
    }

    @Override
    public void createCipherCode(FinanceCipherCodeCreateParams params) {
        //生成卡密
        List<String> codes = financeCipherCodeService.generateCipherCode(params.getCount());
        List<FinanceCipherCode> cipherCodes = new ArrayList<>();
        codes.stream().forEach(code -> {
            FinanceCipherCode cipherCode = new FinanceCipherCode();
            cipherCode.setCipherCode(code);
            cipherCode.setPoints(params.getPoints());
            cipherCode.setTotalAmount(params.getAmount());
            cipherCode.setStatus(CipherStatus.USABLE.getValue());
            cipherCodes.add(cipherCode);
        });
        financeCipherCodeService.insertBatch(cipherCodes);
    }

    @Override
    public HztPage<FinanceCipherCodeVO> listCipherCode(FinanceCipherCodeParam param) {
        return financeCipherCodeService.listCipherCode(param);
    }

    @Override
    public KD100Config getKD100Config() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.KD100.getValue()).one();
        if (Objects.isNull(config)) {
            return null;
        }
        KD100Config kd100Config = JSON.parseObject(config.getConfigValue(), KD100Config.class);
        return kd100Config;
    }

    @Override
    public void saveKD100Config(KD100Config kd100Config) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.KD100.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.KD100.getValue());
            config.setConfigName(SysConfigKeyEnum.KD100.getDescription());
        }
        config.setConfigValue(JSONUtil.toJsonStr(kd100Config));
        configMapper.saveOrUpdateById(config);
    }

    @Override
    public List<ExpendConfigParam> listExpendConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.EXPEND_POINTS.getValue()).one();
        if (Objects.isNull(config)) {
            return Collections.EMPTY_LIST;
        }
        List<ExpendConfigParam> expendConfigParams = JSON.parseArray(config.getConfigValue(), ExpendConfigParam.class);
        return expendConfigParams;
    }

    @Override
    public List<Map<String, String>> listExpendType() {
        return ExpendTypeEnum.enumToMap();
    }

    @Override
    public void saveDefaultPointsConfig(DefaultPointConfig params) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey,
                SysConfigKeyEnum.DEFAULT_POINTS.getValue()).one();

        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.DEFAULT_POINTS.getValue());
            config.setConfigName(SysConfigKeyEnum.DEFAULT_POINTS.getDescription());
        }
        config.setConfigValue(JSONUtil.toJsonStr(params));
        configMapper.saveOrUpdateById(config);
    }

    @Override
    public DefaultPointConfig getDefaultPointsConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey, SysConfigKeyEnum.DEFAULT_POINTS.getValue()).one();
        if (Objects.isNull(config)) {
            return new DefaultPointConfig();
        }
        DefaultPointConfig pointConfig = JSON.parseObject(config.getConfigValue(), DefaultPointConfig.class);
        return pointConfig;
    }

    @Override
    public void saveExpendConfig(List<ExpendConfigParam> params) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey,
                SysConfigKeyEnum.EXPEND_POINTS.getValue()).one();

        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.EXPEND_POINTS.getValue());
            config.setConfigName(SysConfigKeyEnum.EXPEND_POINTS.getDescription());
        }
        config.setConfigValue(JSONUtil.parseArray(params).toString());
//        JSONArray jsonArray = JSONUtil.parseArray(params);
        redisCache.setCacheObject(Constants.GPT_POINT_KEY, config.getConfigValue());
        configMapper.saveOrUpdateById(config);
    }


    @Override
    public HomeViewConfig getHomeConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey,
                SysConfigKeyEnum.HOME_CONFIG.getValue()).one();
        if (Objects.isNull(config)) {
            return new HomeViewConfig();
        }
        HomeViewConfig pointConfig = JSON.parseObject(config.getConfigValue(), HomeViewConfig.class);
        return pointConfig;
    }

    @Override
    public int saveHomeConfig(HomeViewConfig homeViewConfig) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey,
                SysConfigKeyEnum.HOME_CONFIG.getValue()).one();

        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.HOME_CONFIG.getValue());
            config.setConfigName("首页配置");
        }
        config.setConfigValue(JacksonUtil.toJsonString(homeViewConfig));

        return configMapper.saveOrUpdateById(config);
    }

    /**
     * 查询支付配置
     * @return
     */
    @Override
    public PayTypeConfig getPayTypeConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey,
                SysConfigKeyEnum.PAY_WAY.getValue()).one();
        if (Objects.isNull(config)) {
            return new PayTypeConfig();
        }
        PayTypeConfig pointConfig = JSON.parseObject(config.getConfigValue(), PayTypeConfig.class);
        return pointConfig;
    }

    /**
     * 更新支付类型配置
     * @param payTypeConfig
     * @return
     */
    @Override
    public int savePayTypeConfig(PayTypeConfig payTypeConfig) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey,
                SysConfigKeyEnum.PAY_WAY.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.PAY_WAY.getValue());
            config.setConfigName("支付类型配置");
        }
        config.setConfigValue(JacksonUtil.toJsonString(payTypeConfig));
        redisCache.deleteObject(CHAT_EXPIRED_CONFIG_KEY);
        return configMapper.saveOrUpdateById(config);
    }

    @Override
    public EmailConfig getEmailConfig() {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey,
                SysConfigKeyEnum.EMAIL_CONFIG.getValue()).one();
        if (Objects.isNull(config)) {
            return new EmailConfig();
        }
        EmailConfig emailConfig = JSON.parseObject(config.getConfigValue(), EmailConfig.class);
        return emailConfig;
    }

    @Override
    public int saveEmailConfig(EmailConfig EmailConfig) {
        SysConfig config = configMapper.lambdaQuery().eq(SysConfig::getConfigKey,
                SysConfigKeyEnum.EMAIL_CONFIG.getValue()).one();
        if (Objects.isNull(config)) {
            config = new SysConfig();
            config.setConfigKey(SysConfigKeyEnum.EMAIL_CONFIG.getValue());
            config.setConfigName("支付类型配置");
        }
        config.setConfigValue(JacksonUtil.toJsonString(EmailConfig));
        return configMapper.saveOrUpdateById(config);
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }
}
