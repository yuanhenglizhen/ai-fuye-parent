package com.hanzaitu.common.core;

import lombok.Data;

import java.io.Serializable;

/**
  * dubbo 请求参数基数类，需要基础该对象
  */
@Data
public class HztBaseParam implements Serializable {

    private static final long serialVersionUID = 8995085711205450025L;

    public static final String TERMINAL_TYPE = "terminalType";


}