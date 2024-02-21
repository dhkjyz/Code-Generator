package com.mango.maker.meta.enums;

public enum StateTypeEnum{

    STATIC("静态文件","static"),
    DYNAMIC("动态文件","dynamic");

    StateTypeEnum(String description, String value) {
        this.description = description;
        this.value = value;
    }

    private String description;
    private String value;

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }
}
