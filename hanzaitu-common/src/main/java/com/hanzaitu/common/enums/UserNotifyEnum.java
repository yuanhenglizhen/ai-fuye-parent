package com.hanzaitu.common.enums;

public enum UserNotifyEnum {

    /**
     * 邀请好友消息提醒
     */
    INVITE_MESSAGE("亲爱的用户，感谢您邀请好友加入我们的平台！我们很高兴地告诉您，您邀请的好友已于 $date 成功注册，并且您的奖励已经到账。"),

    /**
     * 邀请好友奖励通知
     */
    POLITE_INVITATION("尊敬的用户，您好！我们很高兴地通知您，您的账户已于 $date 成功充值，充值积分为 %d 积分。"),

    /**
     * 回退积分消息通知
     */
    REBATE_POINTS("%s 执行失败， 积分已退回您账户，给您带来的不便敬请谅解。"),

    /**
     * 绘画审核通过提醒
     */
    DRAW_RELEASE_NOTICE("尊敬的用户，您于 %s 发布的绘画已审核通过，请您前往 绘画广场->风格长廊 查看您的作品。 ")
    ;

    private String msg;

    UserNotifyEnum(String msg) {
        this.msg = msg;
    }

    /**
     * 查询消息
     * @return
     */
    public String getMsg() {
        return msg;
    }

}
