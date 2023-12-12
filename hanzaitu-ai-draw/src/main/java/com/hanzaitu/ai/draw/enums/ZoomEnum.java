package com.hanzaitu.ai.draw.enums;


public enum ZoomEnum {

    /**
     * 镜头拉更远值
     */
    ZOOM_HIGH(75),

    /**
     * 镜头拉更近值
     */
    ZOOM_LOW(50);

    private Integer value;


    ZoomEnum(Integer v) {
        this.value = v;
    }

    /**
     * 获取值
     * @return
     */
    public Integer getValue() {
        return value;
    }


}
