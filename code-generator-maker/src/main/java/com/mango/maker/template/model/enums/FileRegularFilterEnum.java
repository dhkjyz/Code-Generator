package com.mango.maker.template.model.enums;

public enum FileRegularFilterEnum {

    START_WITH("前缀匹配","startWiths"),
    END_WITH("后缀匹配","endWiths"),
    CONTAINS("包含","contains"),
    EQUALS("等于","equals"),
    REGEX("正则匹配","regex");


    private String description;
    private String value;


    FileRegularFilterEnum(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return value;
    }

    public static FileRegularFilterEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (FileRegularFilterEnum e : FileRegularFilterEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
