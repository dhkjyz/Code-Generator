package com.mango.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 *文件配置类
 */
@Data
public class TemplateMakeFileConfig {

    private List<FileFilterModel> files;
    private FileGroupConfig fileGroupConfig;

    /**
     * 文件过滤模型
     */

    @Data
    public static class FileFilterModel {
        private String path;
        private List<FileFilterConfig> fileFilterConfig;
    }

    @Data
    public static class FileGroupConfig {
        private String groupKey;
        private String groupName;
        private String condition;
    }



}
