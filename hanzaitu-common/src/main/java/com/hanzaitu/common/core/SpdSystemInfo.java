package com.hanzaitu.common.core;

import org.springframework.stereotype.Component;

@Component
public class SpdSystemInfo implements SystemInfo {

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
