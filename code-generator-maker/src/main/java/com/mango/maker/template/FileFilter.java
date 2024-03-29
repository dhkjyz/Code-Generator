package com.mango.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.mango.maker.template.model.TemplateMakeFileConfig;
import com.mango.maker.template.model.enums.FileRegularFilterEnum;
import com.mango.maker.template.model.enums.FileTypeFilterEnum;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileFilter {
    /**
     * 文件过滤器
     *
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath, List<TemplateMakeFileConfig.FileFilterConfig> fileFilterConfigList) {

        List<File> fileList = FileUtil.loopFiles(filePath);//无论是文件还是目录，统一变成文件列表。

        return fileList.stream()
                .filter(file -> doSingleFileFilter(fileFilterConfigList, file))
                .collect(Collectors.toList());
    }

    /**
     * 对单一文件进行过滤
     *
     * @param filterConfigList      过滤规则
     * @param inputFileAbsolutePath 要过滤文件的绝对值
     * @return
     */
    public static boolean doSingleFileFilter(List<TemplateMakeFileConfig.FileFilterConfig> filterConfigList, File inputFileAbsolutePath) {
        String fileName = inputFileAbsolutePath.getName(); //读取要过滤文件的标题
        String fileContent = FileUtil.readUtf8String(inputFileAbsolutePath); //读取要过滤文件的内容
        boolean result = true;

        if (CollUtil.isEmpty(filterConfigList)) {
            return true;
        }
        String content = null;
        for (TemplateMakeFileConfig.FileFilterConfig fileFilterConfig : filterConfigList) {
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();
            String range = fileFilterConfig.getRange();

            FileTypeFilterEnum enumTypeByValue = FileTypeFilterEnum.getEnumByValue(range);
            if (enumTypeByValue == null) {
                continue;
            }
            switch (enumTypeByValue) {
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
                default:
            }

            FileRegularFilterEnum enumRegularByValue = FileRegularFilterEnum.getEnumByValue(rule);
            if (enumRegularByValue == null) {
                continue;
            }

            switch (enumRegularByValue) {
                case START_WITH:
                    result = content.startsWith(value);
                    break;
                case END_WITH:
                    result = content.endsWith(value);
                    break;
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
            }

            if (!result) {
                return false;
            }
        }

        return true;
    }

}
