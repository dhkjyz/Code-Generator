package com.mango.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * 文件过滤规则
 */

@Data
@Builder
public class FileFilterConfig {
    private String range;
    private String rule;
    private String value;

}
