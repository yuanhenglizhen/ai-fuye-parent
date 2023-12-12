package com.hanzaitu.ai.business.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.hanzaitu.ai.business.domain.CustomerUser;
import com.hanzaitu.ai.business.param.CustomerUserSaveMailParam;
import com.hanzaitu.ai.business.param.LoginPhoneParam;
//import com.hanzaitu.ai.util.MailUtils;
import com.hanzaitu.ai.util.mail.MailerBuilderInstance;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.domain.EmailConfig;
import com.hanzaitu.common.enums.UserNotifyEnum;
import com.hanzaitu.ai.business.mapper.CustomerUserMapper;
import com.hanzaitu.ai.business.param.CustomerUserSaveParam;
import com.hanzaitu.ai.business.param.LoginParam;
import com.hanzaitu.ai.business.service.LoginService;
import com.hanzaitu.ai.business.vo.CustomerUserVO;
import com.hanzaitu.common.config.AppProperties;
import com.hanzaitu.common.config.HanZaiTuConfig;
import com.hanzaitu.common.config.KD100Config;
import com.hanzaitu.common.constant.UserConstants;
import com.hanzaitu.common.core.HztBaseService;
import com.hanzaitu.common.core.redis.RedisUtil;
import com.hanzaitu.common.core.result.ResultException;
import com.hanzaitu.common.core.session.UserSessionDTO;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.enums.ValidStatusEnum;
import com.hanzaitu.common.exception.AuthException;
import com.hanzaitu.common.manager.AsyncManager;
import com.hanzaitu.common.params.DefaultPointConfig;
import com.hanzaitu.common.params.WalletPointParam;
import com.hanzaitu.common.service.FinanceUserWalletService;
import com.hanzaitu.common.service.ISysConfigService;
import com.hanzaitu.common.service.NotifyMsgCommonService;
import com.hanzaitu.common.utils.*;
import com.hanzaitu.common.utils.bean.BeanUtils;
/*import io.github.thundzeng.miniemail.config.MailConfig;
import io.github.thundzeng.miniemail.constant.SmtpHostEnum;
import io.github.thundzeng.miniemail.core.MiniEmail;
import io.github.thundzeng.miniemail.core.MiniEmailFactory;
import io.github.thundzeng.miniemail.core.MiniEmailFactoryBuilder;*/
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author cr
 * 用户登录
 */
@Slf4j
@Service
public class LoginServiceImpl extends HztBaseService implements LoginService {

    private static Pattern PHONE_NUMBER_REGEX = Pattern.compile("^1[3-9]\\d{9}$");

    private static Pattern EMAIL_REGEX = Pattern.compile("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");

    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private FinanceUserWalletService financeUserWalletService;

    @Autowired
    private ISysConfigService sysConfigService;

    @Autowired
    private NotifyMsgCommonService notifyMsgService;

    @Override
    public UserSessionDTO login(LoginParam loginParam) {
        CustomerUser customerUser = validateUserForLogin(loginParam.getUsername(), loginParam.getPassword());

        UserSessionDTO userSessionDTO = new UserSessionDTO();
        BeanUtils.copyProperties(customerUser, userSessionDTO);
        userSessionDTO.setUserName(customerUser.getUserName());
        // 生成accessToken
        String accessToken = TokenUtil.createAccessToken(userSessionDTO.getId());
        userSessionDTO.setAccessToken(accessToken);
        userSessionDTO.setNickName(customerUser.getNickName());
        userSessionDTO.setPhoneNumber(customerUser.getPhoneNumber());
        // 设置session
        Long sessionExpireTime = appProperties.getAuth().getSessionExpireTime();

        RedisUtil.set(TokenUtil.getAccessTokenKey(accessToken, appProperties.getAuth().getAccessTokenKeyPrefix()),
                JSON.toJSONString(userSessionDTO),
                sessionExpireTime, TimeUnit.MINUTES);
        log.info("user login success->", loginParam.getUsername());
        return userSessionDTO;
    }

    @Override
    public UserSessionDTO loginPhoneNumber(LoginPhoneParam loginParam) {
        CustomerUser customerUser = validateUserForLogin(loginParam.getPhoneNumber());

        String realCode = (String) RedisUtil.get(HanZaiTuConfig.VERIFY_CODE_PREFIX + loginParam.getPhoneNumber());
        if (!loginParam.getVerificationCode().equals(realCode)){
            throw ResultException.createResultException("验证码错误");
        }

        UserSessionDTO userSessionDTO = new UserSessionDTO();
        BeanUtils.copyProperties(customerUser, userSessionDTO);
        userSessionDTO.setUserName(customerUser.getUserName());
        // 生成accessToken
        String accessToken = TokenUtil.createAccessToken(userSessionDTO.getId());
        userSessionDTO.setAccessToken(accessToken);
        userSessionDTO.setNickName(customerUser.getNickName());
        userSessionDTO.setPhoneNumber(customerUser.getPhoneNumber());
        // 设置session
        Long sessionExpireTime = appProperties.getAuth().getSessionExpireTime();

        RedisUtil.set(TokenUtil.getAccessTokenKey(accessToken, appProperties.getAuth().getAccessTokenKeyPrefix()),
                JSON.toJSONString(userSessionDTO),
                sessionExpireTime, TimeUnit.MINUTES);
        RedisUtil.del(HanZaiTuConfig.VERIFY_CODE_PREFIX + loginParam.getPhoneNumber());
        log.info("user login success->", loginParam.getPhoneNumber());
        return userSessionDTO;
    }

    @Override
    public void logout(String accessToken) {
        String accessTokenKey = TokenUtil.getAccessTokenKey(accessToken, appProperties.getAuth().getAccessTokenKeyPrefix());
        RedisUtil.del(accessTokenKey);
        log.info("user logout success->{}", accessToken);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(CustomerUserSaveParam param) {
        validatePhoneNumber(param.getPhoneNumber());
        List<CustomerUser> customerUsers = customerUserMapper.selectList(CustomerUser::getPhoneNumber, param.getPhoneNumber(),
                CustomerUser::getDelFlag, 0);
        if (!CollectionUtils.isEmpty(customerUsers)) {
            throw ResultException.createResultException("手机号已存在，无法注册");
        }
        String realCode = (String) RedisUtil.get(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getPhoneNumber());
        if (!param.getVerificationCode().equals(realCode)){
            throw ResultException.createResultException("验证码错误");
        }
        DefaultPointConfig config = sysConfigService.getDefaultPointsConfig();
        Integer invitePoints = 20;
        Integer initPoints = 20;
        if (Objects.nonNull(config)) {
            invitePoints = config.getInvitePoints();
            initPoints = config.getInitPoints();
        }
        CustomerUser customerUser = BeanUtils.copyProperties(param, CustomerUser.class);
        if (StringUtils.isNotBlank(param.getInviteCode())) {
            CustomerUser checkInviteUser = customerUserMapper.selectOne(CustomerUser::getInviteCode, param.getInviteCode(),
                    CustomerUser::getDelFlag, 0);
            if (Objects.isNull(checkInviteUser)){
                throw ResultException.createResultException("邀请码不存在");
            }
            customerUser.setInviteUserId(checkInviteUser.getId());
            addInvitePoint(checkInviteUser.getId(), invitePoints);

            notifyMsgService.publishUserMessage(checkInviteUser.getId(), UserNotifyEnum.INVITE_MESSAGE.getMsg());
        }

        customerUser.setInviteCode(generateInviteCode(8));
        customerUser.setUserName(customerUser.getPhoneNumber());
        customerUser.setDelFlag(0);
        customerUser.setStatus(1);
        customerUser.setPassword(MD5Utils.encrypt(param.getPassword()));
        customerUser.setNickName(RandomUtil.getRandomString(6).toUpperCase());

        customerUserMapper.saveOrUpdateById(customerUser);
        CustomerUserVO customerUserVO = BeanUtils.copyProperties(customerUser, CustomerUserVO.class);
        financeUserWalletService.initUserWallet(customerUser.getId(), initPoints);
        //组装邀请链接
        customerUserVO.setInviteUrl(StringUtils.join(appProperties.getInviteLink(),"?",customerUser.getInviteCode()));
        RedisUtil.del(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getPhoneNumber());
        LoginParam loginParam = new LoginParam();
        loginParam.setPassword(param.getPassword());
        loginParam.setUsername(param.getPhoneNumber());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean registerEmail(CustomerUserSaveMailParam param) {
        validateEmail(param.getEmail());
        List<CustomerUser> customerUsers = customerUserMapper.selectList(CustomerUser::getEmail, param.getEmail(),
                CustomerUser::getDelFlag, 0);
        if (!CollectionUtils.isEmpty(customerUsers)) {
            throw ResultException.createResultException("邮箱已存在，无法注册");
        }
        String realCode = (String) RedisUtil.get(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getEmail());
        if (!param.getVerificationCode().equals(realCode)){
            throw ResultException.createResultException("验证码错误");
        }
        DefaultPointConfig config = sysConfigService.getDefaultPointsConfig();
        Integer invitePoints = 20;
        Integer initPoints = 20;
        if (Objects.nonNull(config)) {
            invitePoints = config.getInvitePoints();
            initPoints = config.getInitPoints();
        }
        CustomerUser customerUser = BeanUtils.copyProperties(param, CustomerUser.class);
        if (StringUtils.isNotBlank(param.getInviteCode())) {
            CustomerUser checkInviteUser = customerUserMapper.selectOne(CustomerUser::getInviteCode, param.getInviteCode(), CustomerUser::getDelFlag, 0);
            if (Objects.isNull(checkInviteUser)){
                throw ResultException.createResultException("邀请码不存在");
            }
            customerUser.setInviteUserId(checkInviteUser.getId());
            addInvitePoint(checkInviteUser.getId(), invitePoints);
            notifyMsgService.publishUserMessage(checkInviteUser.getId(), UserNotifyEnum.INVITE_MESSAGE.getMsg());
        }

        customerUser.setInviteCode(generateInviteCode(8));
        customerUser.setUserName(customerUser.getEmail());
        customerUser.setDelFlag(0);
        customerUser.setStatus(1);
        customerUser.setEmail(customerUser.getEmail());
        customerUser.setPassword(MD5Utils.encrypt(param.getPassword()));
        customerUser.setNickName(RandomUtil.getRandomString(6).toUpperCase());
        customerUserMapper.saveOrUpdateById(customerUser);
        CustomerUserVO customerUserVO = BeanUtils.copyProperties(customerUser, CustomerUserVO.class);
        financeUserWalletService.initUserWallet(customerUser.getId(), initPoints);
        //组装邀请链接
        customerUserVO.setInviteUrl(StringUtils.join(appProperties.getInviteLink(),"?",customerUser.getInviteCode()));
        RedisUtil.del(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getEmail());
        /*LoginParam loginParam = new LoginParam();
        loginParam.setPassword(param.getPassword());
        loginParam.setUsername(param.getEmail());*/
        //return login(loginParam);
        return true;
    }

    private String generateInviteCode(Integer count) {
        String code = "";
        while (true) {
            code = GenerateCodeUtil.generateCode(UserConstants.INVITE_CODE_LENGTH);
            CustomerUser customerUser = customerUserMapper.selectOne(CustomerUser::getInviteCode, code);
            if (Objects.isNull(customerUser)) {
                break;
            }
        }
        return code;
    }
    @Override
    public void resetPassword(CustomerUserSaveParam param) {
        String realCode = (String) RedisUtil.get(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getPhoneNumber());
        if (!param.getVerificationCode().equals(realCode)){
            throw ResultException.createResultException("验证码错误");
        }
        CustomerUser customerUser = customerUserMapper.selectOne(CustomerUser::getPhoneNumber,
                param.getPhoneNumber(), CustomerUser::getDelFlag, 0);
        customerUser.setPassword(MD5Utils.encrypt(param.getPassword()));
        customerUserMapper.saveOrUpdateById(customerUser);
        if (Objects.nonNull(UserThreadLocal.get())) {
            logout(UserThreadLocal.get().getAccessToken());
        }
        RedisUtil.del(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getPhoneNumber());
    }

    @Override
    public void resetPasswordEmail(CustomerUserSaveMailParam param) {
        String realCode = (String) RedisUtil.get(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getEmail());
        if (!param.getVerificationCode().equals(realCode)){
            throw ResultException.createResultException("验证码错误");
        }
        CustomerUser customerUser = customerUserMapper.selectOne(CustomerUser::getEmail,
                param.getEmail(), CustomerUser::getDelFlag, 0);
        customerUser.setPassword(MD5Utils.encrypt(param.getPassword()));
        customerUserMapper.saveOrUpdateById(customerUser);
        if (Objects.nonNull(UserThreadLocal.get())) {
            logout(UserThreadLocal.get().getAccessToken());
        }
        RedisUtil.del(HanZaiTuConfig.VERIFY_CODE_PREFIX + param.getEmail());
    }

    @Override
    public void sendRegisterCode(String phoneNumber) {
        validatePhoneNumber(phoneNumber);
        String code = RandomNumUtils.getVerificationCode();
        KD100Config config = sysConfigService.getKD100Config();
        RedisUtil.set(HanZaiTuConfig.VERIFY_CODE_PREFIX + phoneNumber, code, 5L, TimeUnit.MINUTES);
        SmsUtils.sendKD100Sms(config,phoneNumber,code);

    }

    @Override
    public AjaxResult sendMailRegisterCode(String email) {
        validateEmail(email);
        String code = RandomNumUtils.getVerificationCode();
        RedisUtil.set(HanZaiTuConfig.VERIFY_CODE_PREFIX + email, code, 5L, TimeUnit.MINUTES);
        EmailConfig emailConfig = sysConfigService.getEmailConfig();
        if (ObjUtil.isEmpty(emailConfig)) {
            throw ResultException.createResultException("邮箱未配置");
        }
        return sendVerifyCode(emailConfig,email,code) ? AjaxResult.success() : AjaxResult.error("邮件发送失败");
    }

    private boolean sendVerifyCode(EmailConfig emailConfig, String emailAddr, String code) {
        if (StringUtils.isEmpty(emailConfig.getSendContent())
                || !emailConfig.getSendContent().contains("$code$")) {
            log.error("邮件验证码配置有误！！");
            return false;
        }
        try {
            AsyncManager.me().execute(new TimerTask() {
                @Override
                public void run() {
                    String content = emailConfig.getSendContent().replace("$code$",
                            code) + "。验证码有效期为5分钟。";
                    log.debug("{} 开始发送 -> " + content, emailAddr);
                    try {
                        Email email = EmailBuilder.startingBlank()
                                .from(emailConfig.getSendName(), emailConfig.getEmail())
                                .to("广大用户", emailAddr)
                                .withSubject(emailConfig.getSendTitle())
                                .withPlainText(content)
                                .clearSentDate()
                                .buildEmail();
                        Mailer mailer = MailerBuilderInstance.getInstance(emailConfig);
                        mailer.sendMail(email);
                        log.debug("{} 发送完毕 -> " + content, emailAddr);
                    } catch (Exception e){
                        log.error(e.toString());
                        log.debug("{} 发送失败 -> " + content, emailAddr);
                        throw e;
                    }
                }
            });
            return true;
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
    }

    /**
     * 验证用户和密码
     * @param username 用户名
     * @param password 密码
     * @return 用户信息
     */
    private CustomerUser validateUserForLogin(String username, String password) {
        LambdaQueryChainWrapper<CustomerUser> queryWrapper = customerUserMapper.lambdaQuery();
        queryWrapper.eq(CustomerUser::getUserName, username).or().eq(CustomerUser::getPhoneNumber,username);
        CustomerUser user = customerUserMapper.selectOne(queryWrapper.getWrapper());
        if (user == null) {
            throw ResultException.createResultException("用户不存在");
        }
        // 验证用户状态
        if (!ValidStatusEnum.VALID.getValue().equals(user.getStatus())) {
            throw ResultException.createResultException("账号状态无效，请联系管理员");
        }
        // 验证用户密码
        if (!user.getPassword().equals(MD5Utils.encrypt(password))) {
            throw ResultException.createResultException("登录名或密码错误");
        }
        return user;
    }

    /**
     * 验证用户和密码
     * @param phoneNumber 手机号
     * @return 用户信息
     */
    private CustomerUser validateUserForLogin(String phoneNumber) {
        LambdaQueryChainWrapper<CustomerUser> queryWrapper = customerUserMapper.lambdaQuery();
        queryWrapper.eq(CustomerUser::getPhoneNumber, phoneNumber);
        CustomerUser user = customerUserMapper.selectOne(queryWrapper.getWrapper());
        if (user == null) {
            throw AuthException.createAuthException("手机号未注册");
        }
        // 验证用户状态
        if (!ValidStatusEnum.VALID.getValue().equals(user.getStatus())) {
            throw ResultException.createResultException("账号状态无效，请联系管理员");
        }
        return user;
    }

    /**
     * 验证手机号有效性
     * @param phoneNumber
     */
    private void validatePhoneNumber(String phoneNumber) {
        if(!PHONE_NUMBER_REGEX.matcher(phoneNumber).matches()){
            throw ResultException.createResultException("手机号格式不正确");
        }
    }

    /**
     * 验证手机号有效性
     * @param email
     */
    private void validateEmail(String email) {
        if(!EMAIL_REGEX.matcher(email).matches()){
            throw ResultException.createResultException("邮箱格式不正确");
        }
    }


    private void security(){

    }

    private void addInvitePoint(Long inviteUserId, Integer points){
        WalletPointParam param = new WalletPointParam();
        param.setPoints(points);
        param.setUserId(inviteUserId);
        financeUserWalletService.updateWalletPoints(param);
    }

}
