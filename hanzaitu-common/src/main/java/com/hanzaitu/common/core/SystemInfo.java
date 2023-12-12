package com.hanzaitu.common.core;

public interface SystemInfo {


    /**
     * 获取系统版本号
     * @return
     */
    String getVersion();

    /**
     * 获取系统发版的脚本路径
     * @return
     */
    default String getSystemSqlPath(){
        return "classpath:sql";
    }

    /**
     * 获取已更新的脚本文件路径
     */
    default String getUpdatedSqlPath(){
        return "sql";
    }
}
