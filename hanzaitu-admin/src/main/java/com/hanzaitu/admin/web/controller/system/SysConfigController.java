package com.hanzaitu.admin.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.hanzaitu.admin.web.controller.common.BaseController;
import com.hanzaitu.common.config.KD100Config;
import com.hanzaitu.common.domain.EmailConfig;
import com.hanzaitu.common.domain.HomeViewConfig;
import com.hanzaitu.common.domain.PayTypeConfig;
import com.hanzaitu.common.params.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hanzaitu.common.annotation.Log;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.core.page.TableDataInfo;
import com.hanzaitu.common.enums.BusinessType;
import com.hanzaitu.common.utils.poi.ExcelUtil;
import com.hanzaitu.common.domain.SysConfig;
import com.hanzaitu.common.service.ISysConfigService;

/**
 * 参数配置 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController
{
    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config)
    {
        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:config:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config)
    {
        List<SysConfig> list = configService.selectConfigList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(configService.selectConfigById(id));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public AjaxResult getConfigKey(@PathVariable String configKey)
    {
        return success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysConfig config)
    {
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(getUsername());
        return toAjax(configService.insertConfig(config));
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysConfig config)
    {
        if (!configService.checkConfigKeyUnique(config))
        {
            return error("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(getUsername());
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 删除参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        configService.deleteConfigByIds(ids);
        return success();
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache()
    {
        configService.resetConfigCache();
        return success();
    }

    @ApiOperation(value = "获取联系我们配置", httpMethod = "GET")
    @GetMapping("getContactUs")
    public AjaxResult getContactUs() {
        return AjaxResult.success(configService.getContactUs());
    }

    @ApiOperation(value = "保存联系我们", httpMethod = "POST")
    @PostMapping("saveContactUs")
    public AjaxResult saveContactUs(@Validated @RequestBody ContactUsParam param) {
        configService.saveContactUs(param);
        return AjaxResult.success();
    }

    @ApiOperation(value = "获取微信支付配置", httpMethod = "GET")
    @GetMapping("getWxPayConfig")
    public AjaxResult getWxPayConfig() {
        return AjaxResult.success(configService.getWxPayConfig());
    }

    @ApiOperation(value = "保存微信支付配置", httpMethod = "POST")
    @PostMapping("saveWxPayConfig")
    public AjaxResult saveWxPayConfig(@Validated @RequestBody WxPayConfigParam param) {
        configService.saveWxPayConfig(param);
        return AjaxResult.success();
    }


    @ApiOperation(value = "获取支付宝支付配置", httpMethod = "GET")
    @GetMapping("getAliPayConfig")
    public AjaxResult getAliPayConfig() {
        return AjaxResult.success(configService.getAliPayConfig());
    }

    @ApiOperation(value = "保存支付宝支付配置", httpMethod = "POST")
    @PostMapping("saveAliPayConfig")
    public AjaxResult saveAliPayConfig(@Validated @RequestBody AliPayConfigParam param) {
        configService.saveAliPayConfig(param);
        return AjaxResult.success();
    }

    @ApiOperation(value = "获取支付宝APP支付配置", httpMethod = "GET")
    @GetMapping("getAliPayAppConfig")
    public AjaxResult getAliPayAppConfig() {
        return AjaxResult.success(configService.getAliPayConfig());
    }

    @ApiOperation(value = "保存支付宝APP支付配置", httpMethod = "POST")
    @PostMapping("saveAliPayAppConfig")
    public AjaxResult saveAliPayAppConfig(@Validated @RequestBody AliPayAppConfigParam param) {
        configService.saveAliPayAppConfig(param);
        return AjaxResult.success();
    }

    @ApiOperation(value = "获取源支付支付配置", httpMethod = "GET")
    @GetMapping("getYPayConfig")
    public AjaxResult getYPayConfig() {
        return AjaxResult.success(configService.getYPayConfig());
    }

    @ApiOperation(value = "保存源支付支付配置", httpMethod = "POST")
    @PostMapping("saveYPayConfig")
    public AjaxResult saveYPayConfig(@Validated @RequestBody YPayConfigParam param) {
        configService.saveYPayConfig(param);
        return AjaxResult.success();
    }

    @ApiOperation(value = "获取支付积分配置", httpMethod = "GET")
    @GetMapping("getPayToPointsConfig")
    public AjaxResult getPayToPointsConfig() {
        return AjaxResult.success(configService.getPayToPointsConfig());
    }

    @ApiOperation(value = "保存支付积分配置", httpMethod = "POST")
    @PostMapping("savePayToPointsConfig")
    public AjaxResult savePayToPointsConfig(@Validated @RequestBody List<PayToPointConfigParam> params) {
        configService.savePayToPointsConfig(params);
        return AjaxResult.success();
    }

    @ApiOperation(value = "生成卡密", httpMethod = "POST")
    @PostMapping("createCipherCode")
    public AjaxResult createCipherCode(@Validated @RequestBody FinanceCipherCodeCreateParams params) {
        configService.createCipherCode(params);
        return AjaxResult.success();
    }

    @ApiOperation(value = "查看卡密", httpMethod = "GET")
    @GetMapping("listCipherCode")
    public AjaxResult listCipherCode(FinanceCipherCodeParam param) {
        return AjaxResult.success(configService.listCipherCode(param));
    }

    @ApiOperation(value = "获取快递100配置", httpMethod = "GET")
    @GetMapping("getKD100Config")
    public AjaxResult getKD100Config() {
        return AjaxResult.success(configService.getKD100Config());
    }

    @ApiOperation(value = "保存快递100配置", httpMethod = "POST")
    @PostMapping("saveKD100Config")
    public AjaxResult saveKD100Config(@Validated @RequestBody KD100Config kd100Config) {
        configService.saveKD100Config(kd100Config);
        return AjaxResult.success();
    }

    @ApiOperation(value = "保存积分消耗配置", httpMethod = "POST")
    @PostMapping("saveExpendConfig")
    public AjaxResult saveExpendConfig(@Validated @RequestBody List<ExpendConfigParam> params) {
        configService.saveExpendConfig(params);
        return AjaxResult.success();
    }

    @ApiOperation(value = "获取积分消耗配置", httpMethod = "GET")
    @GetMapping("getExpendConfig")
    public AjaxResult getExpendConfig() {
        return AjaxResult.success(configService.listExpendConfig());
    }

    @ApiOperation(value = "获取积分消耗类型", httpMethod = "GET")
    @GetMapping("listExpendType")
    public AjaxResult listExpendType() {
        return AjaxResult.success(configService.listExpendType());
    }

    @ApiOperation(value = "保存默认积分配置", httpMethod = "POST")
    @PostMapping("saveDefaultPointsConfig")
    public AjaxResult saveDefaultPointsConfig(@Validated @RequestBody DefaultPointConfig params) {
        configService.saveDefaultPointsConfig(params);
        return AjaxResult.success();
    }

    @ApiOperation(value = "获取默认积分配置", httpMethod = "GET")
    @GetMapping("getDefaultPointsConfig")
    public AjaxResult getDefaultPointsConfig() {
        return AjaxResult.success(configService.getDefaultPointsConfig());
    }

    @ApiOperation(value = "获取首页配置", httpMethod = "GET")
    @GetMapping("getHomeConfig")
    public AjaxResult getHomeConfig() {
        return AjaxResult.success(configService.getHomeConfig());
    }

    @ApiOperation(value = "更新首页配置", httpMethod = "POST")
    @PostMapping("saveHomeConfig")
    public AjaxResult getHomeConfig(@Validated @RequestBody HomeViewConfig homeViewConfig) {
        return AjaxResult.success(configService.saveHomeConfig(homeViewConfig));
    }

    @ApiOperation(value = "获取支付类型配置", httpMethod = "GET")
    @GetMapping("getPayType")
    public AjaxResult getPayType() {

        return AjaxResult.success(configService.getPayTypeConfig());
    }

    @ApiOperation(value = "更新支付类型", httpMethod = "POST")
    @PostMapping("savePayType")
    public AjaxResult savePayType(@Validated @RequestBody PayTypeConfig payTypeConfig) {
        return AjaxResult.success(configService.savePayTypeConfig(payTypeConfig));
    }

    @ApiOperation(value = "获取邮箱配置", httpMethod = "GET")
    @GetMapping("getMailConfig")
    public AjaxResult getMailConfig() {
        return AjaxResult.success(configService.getEmailConfig());
    }

    @ApiOperation(value = "更新邮箱配置", httpMethod = "POST")
    @PostMapping("saveEmailConfig")
    public AjaxResult saveEmailConfig(@RequestBody EmailConfig emailConfig) {
        return AjaxResult.success(configService.saveEmailConfig(emailConfig));
    }

}
