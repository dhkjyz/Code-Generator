package com.mango.maker.meta;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.mango.maker.meta.enums.FileTypeEnum;
import com.mango.maker.meta.enums.StateTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MetaVolidate {
    public static void doGenerate(Meta meta) {
        volidateBasicMessage(meta);

        volidateFileMessage(meta);
        volidateModelMessage(meta);


    }

    private static void volidateBasicMessage(Meta meta) {
        //1.基本信息设置默认与校验
        String name = meta.getName();
        String defaultName = StrUtil.blankToDefault(name, "acm-template-pro-generator");
        meta.setName(defaultName);

        String description = meta.getDescription();
        String defaultDescription = StrUtil.blankToDefault(description, "我的模版代码生成器");
        meta.setDescription(defaultDescription);

        String basePackage = meta.getBasePackage();
        String defaultBasePackage = StrUtil.blankToDefault(basePackage, "com.mango");
        meta.setBasePackage(defaultBasePackage);

        String version = meta.getVersion();
        String defaultVersion = StrUtil.blankToDefault(version, "1.0");
        meta.setVersion(defaultVersion);

        String author = meta.getAuthor();
        String defaultMango = StrUtil.blankToDefault(author, "mango");
        meta.setAuthor(defaultMango);

        String createTime = meta.getCreateTime();
        //todo 获取当前时间
        Date date = new Date();
        String defaultDate = StrUtil.blankToDefault(createTime, date.toString());
        meta.setCreateTime(defaultDate);
    }

    private static void volidateFileMessage(Meta meta) {
        //2 验证fileConfig
        Meta.FileConfigDTO fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        String sourceRootPath = fileConfig.getSourceRootPath();
        if (StrUtil.isBlank(sourceRootPath)) {
            throw new MetaException("sourceRootPath不能为空");
        }

        String inputRootPath = fileConfig.getInputRootPath();
        //todo 设置默认值为.source+ sourceRootPath 的最后一个层级路径
        String defaultInputRootPath = StrUtil.blankToDefault(inputRootPath, ".source" + File.separator + FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString());
        fileConfig.setInputRootPath(defaultInputRootPath);

        String outputRootPath = fileConfig.getOutputRootPath();
        String defaultOutputPath = StrUtil.blankToDefault(outputRootPath, System.getProperty("user.dir") + File.separator + "generated");
        fileConfig.setOutputRootPath(defaultOutputPath);

        String fileConfigType = fileConfig.getType();
        String defaultFileConfigType = StrUtil.blankToDefault(fileConfigType, FileTypeEnum.DIR.getValue());
        fileConfig.setType(defaultFileConfigType);

        List<Meta.FileConfigDTO.FileInfo> files = fileConfig.getFiles();

        for (Meta.FileConfigDTO.FileInfo fileInfo : files) {

            String groupKey = fileInfo.getGroupKey();
            if (groupKey != null) {
                continue;
            }
            String inputPath = fileInfo.getInputPath();
            if (fileInfo.getType() != FileTypeEnum.GROUP.getValue() && StrUtil.isBlank(inputPath)) {
                throw new RuntimeException("inputPath不能为空");
            }

            String outputPath = fileInfo.getOutputPath();
            String defaultoutputPath = StrUtil.blankToDefault(outputPath, inputPath);
            fileInfo.setOutputPath(defaultoutputPath);
            //todo inputPath 有文件后缀如java为file，否则为dir
            String suffix = FileUtil.getSuffix(inputPath);
            String fileType = fileInfo.getType();
            if (suffix == null) {
                StrUtil.blankToDefault(fileType, FileTypeEnum.DIR.getValue());
            } else {
                StrUtil.blankToDefault(fileType, FileTypeEnum.FILE.getValue());
            }

            String generateType = fileInfo.getGenerateType();
            if ("ftl".equals(suffix)) {
                fileInfo.setGenerateType(StateTypeEnum.DYNAMIC.getValue());
            } else {
                fileInfo.setGenerateType(StateTypeEnum.STATIC.getValue());
            }
        }
    }

    private static void volidateModelMessage(Meta meta) {
        List<Meta.ModelConfigDTO.ModelInfo> modelList = meta.getModelConfig().getModels();
        for (Meta.ModelConfigDTO.ModelInfo modelInfo : modelList) {
            String fieldName = modelInfo.getFieldName();
            String groupKey = modelInfo.getGroupKey();

            /**
             *
             */
            if (StrUtil.isNotEmpty(groupKey)) {
                List<Meta.ModelConfigDTO.ModelInfo> subModelList = modelInfo.getModels();
                String allArgStr = modelInfo.getModels().stream()
                        .map(subModelInfo -> String.format("\"--%s\"", subModelInfo.getFieldName()))
                        .collect(Collectors.joining(","));

                modelInfo.setAllArgsStr(allArgStr);
                continue;
            }
            if (StrUtil.isBlank(fieldName)) {
                throw new MetaException("fieldName不能为空");
            }
        }
    }

}
