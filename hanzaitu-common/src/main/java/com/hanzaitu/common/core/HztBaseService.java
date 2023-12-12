package com.hanzaitu.common.core;

import com.hanzaitu.common.core.session.UserSession;
import com.hanzaitu.common.core.session.UserThreadLocal;
import com.hanzaitu.common.utils.Assert;

public class HztBaseService {

    /**
     * mybatis结果转换为Bool类型
     * @param result
     * @return
     */
    public boolean dbretBool(Integer result) {
        return null != result && result >= 1;
    }

    /**
     * 检查是否数据库操作成功，如果操作失败，则抛出异常
     * @param result
     */
    public void checkDbret(Integer result){
        Assert.dbSuccess(result, "数据库操作失败");
    }


    /**
     * 获取用户Session信息
     * @return
     */
    public UserSession getUserSession(){
        return UserThreadLocal.get();
    }
}
