package com.mango.maker.template.utils;

import com.mango.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模版工具类
 */
public class TemplateMakerUtils {
    public static List<Meta.FileConfigDTO.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfigDTO.FileInfo> fileInfoList) {
        //先获取到所有分组
        List<Meta.FileConfigDTO.FileInfo> groupFileInfoList = fileInfoList.stream().filter(fileInfo -> fileInfo.getGroupKey() != null).collect(Collectors.toList());
        //获取所有分组内的文件列表
        List<Meta.FileConfigDTO.FileInfo> groupInnerFileInfoList = groupFileInfoList.stream().flatMap(fileInfo -> fileInfo.getFiles().stream()).collect(Collectors.toList());
        //获取所有分组内文件输入路径结合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream().map(Meta.FileConfigDTO.FileInfo::getInputPath).collect(Collectors.toSet());
        //移除所有名称在set中的外层文件
        return fileInfoList.stream().filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath())).collect(Collectors.toList());
    }
}
