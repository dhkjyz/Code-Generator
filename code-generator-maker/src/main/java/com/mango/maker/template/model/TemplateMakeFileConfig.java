package com.mango.maker.template.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件配置类
 */
@Data
public class TemplateMakeFileConfig {

    private List<TemplateMakeFileConfig.FileFilterModel> files = new ArrayList<>();
    private TemplateMakeFileConfig.FileGroupConfig fileGroupConfig;


    @Data
    public static class FileGroupConfig {
        private String groupKey;
        private String groupName;
        private String condition;
    }

    /**
     * 文件过滤模型
     */

    @Data
    @Builder
    public static class FileFilterModel {
        private String path;
        private String condition;

        private List<TemplateMakeFileConfig.FileFilterConfig> fileFilterConfig;


    }

    @Data
    @Builder
    public static class FileFilterConfig {
        private String range;
        private String rule;
        private String value;

    }


}
