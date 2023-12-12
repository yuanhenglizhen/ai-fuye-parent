package com.hanzaitu.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author ruoyi
 */
public class CacheConstants
{

    public static final String ROOT_CACHE_KEY = "hzt:";

    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 聊天记录 redis key
     */
    public static final String SESSION_LOG = ROOT_CACHE_KEY+"session_log:";

    /**
     * 缓存窗口名称
     */
    public static final String CHAT_WIN_TITLE = ROOT_CACHE_KEY+"chat_win_title:";


    /**
     * 提示词缓存
     */
    public static final String CHAT_PROMPT = ROOT_CACHE_KEY+"chat_prompt:";



    /**
     * 绘画任务缓存
     */
    public static final String DRAW_TASK_CACHE = ROOT_CACHE_KEY+"draw_task_cache:";



    /**
     * 绘画任务提示词缓存
     */
    public static final String DRAW_TASK_PROMPT_CACHE = ROOT_CACHE_KEY+"draw_task_prompt_cache:";

    /**
     * chat_gpt key队列
     */
    public static final String CHAT_QUEUE_KEY = ROOT_CACHE_KEY+"chat_queue_key";


    /**
     * chat_gpt 聊天过期策略key
     */
    public static final String CHAT_EXPIRED_CONFIG_KEY = ROOT_CACHE_KEY+"chat_expired_config";

    /**
     * chat_gpt 聊天过期策略key
     */
    public static final String CHAT_WINDOW_EXPIRED = ROOT_CACHE_KEY+"chat_window_expired";


    /**
     * 充值预扣金額
     */
    public static final String INTEGRAL_DEDUCTION = ROOT_CACHE_KEY+"integral_deduction";


    /**
     * 用户点赞记录
     */
    public static final String DRAW_PRAISE = ROOT_CACHE_KEY+"draw_praise";
}
