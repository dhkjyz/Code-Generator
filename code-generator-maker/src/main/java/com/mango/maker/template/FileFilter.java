package com.mango.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.mango.maker.template.model.FileFilterConfig;
import com.mango.maker.template.model.enums.FileRegularFilterEnum;
import com.mango.maker.template.model.enums.FileTypeFilterEnum;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class FileFilter {
    /**
     * 对某个文件/目录进行过滤，返回已过滤后的文件列表，再把列表放到制作模板的方法中
     * @param filePath
     * @param fileFilterConfigList
     * @return
     */
    public static List<File> doFilter(String filePath , List<FileFilterConfig> fileFilterConfigList){

        List<File> fileList = FileUtil.loopFiles(filePath);

        return fileList.stream()
                .filter(file-> doSingleFileFilter(fileFilterConfigList,file))
                .collect(Collectors.toList());
    }

    /**
     * 对单一文件进行过滤
     * @param filterConfigList
     * @param inputFileAbsolutePath
     * @return
     */
    public static boolean doSingleFileFilter(List<FileFilterConfig> filterConfigList , File inputFileAbsolutePath){
        String fileName = inputFileAbsolutePath .getName();
        String fileContent = FileUtil.readUtf8String(inputFileAbsolutePath);
        boolean result = true;

        if (CollUtil.isEmpty(filterConfigList)){
            return true;
        }
        String content = null;
        for (FileFilterConfig fileFilterConfig : filterConfigList) {
            String rule = fileFilterConfig.getRule();
            String value = fileFilterConfig.getValue();
            String range = fileFilterConfig.getRange();

            FileTypeFilterEnum enumTypeByValue = FileTypeFilterEnum.getEnumByValue(range);
            if (enumTypeByValue == null) {
                continue;
            }
            switch (enumTypeByValue){
                case FILE_NAME:
                    content = fileName ;
                    break;
                case FILE_CONTENT:
                    content = fileContent ;
                    break;
                default:
            }

            FileRegularFilterEnum enumRegularByValue = FileRegularFilterEnum.getEnumByValue(rule);
            if (enumRegularByValue == null) {
                continue;
            }

            switch (enumRegularByValue){
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
                    result= content.matches(value);
                    break;
            }

            if (!result){
                return false;
            }
        }

        return true;
    }

}
