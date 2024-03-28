package com.mango.maker.template.model.enums;

public enum FileTypeFilterEnum {

    FILE_NAME("文件名", "fileName"),
    FILE_CONTENT("文件内容", "fileContent");

    private String description;
    private String values;


    FileTypeFilterEnum(String description, String values) {
        this.description = description;
        this.values = values;
    }

    public String getDescription() {
        return description;
    }

    public String getValue() {
        return values;
    }


    public static FileTypeFilterEnum getEnumByValue(String value) {
        if (value == null) {
            return null;
        }
        for (FileTypeFilterEnum e : FileTypeFilterEnum.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }
}
