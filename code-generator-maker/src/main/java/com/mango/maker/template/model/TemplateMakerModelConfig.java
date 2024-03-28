package com.mango.maker.template.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板生成配置实体类
 */

@Data
public class TemplateMakerModelConfig {
    private List<ModelInfoConfig> models = new ArrayList<>();
    private TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig;

    /**
     * 文件过滤模型
     */
    @Data
    @Builder
    public static class ModelInfoConfig {
        private String fieldName;
        private String type;
        private String description;
        private Object defaultValue;
        private String abbr;
        //用于替换哪些文本
        private String replaceText;


    }

    @Data
    public static class ModelGroupConfig {
        private String groupKey;
        private String groupName;
        private String condition;
        private String type;
        private String description;
    }


}
