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
    private  boolean loop = false;
    private String author="mango";

    private String message="sum：";
}
