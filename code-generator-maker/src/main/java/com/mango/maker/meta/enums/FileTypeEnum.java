package com.mango.maker.meta.enums;

public enum FileTypeEnum {

    DIR("文件夹","dir"),
    FILE("文件","file");

    FileTypeEnum(String description, String value) {
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
