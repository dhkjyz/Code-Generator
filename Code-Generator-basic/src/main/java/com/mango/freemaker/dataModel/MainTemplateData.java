package com.mango.freemaker.dataModel;

import lombok.Data;

/**
 *
 */
@Data
public class MainTemplateData {
    /**
     * loop 控制循环 ，默认不使用循环
     */
    public  boolean loop = false;
    public String author="mango";

    public String message="sum：";
}
