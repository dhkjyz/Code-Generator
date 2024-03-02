package com.mango.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data

public class TemplateMakeFileConfig {

    private List<FileFilterModel> fileFilterModel;

    @Data
    public static class FileFilterModel {
        private String path;
        private List<FileFilterConfig> fileFilterConfig;
    }



}
