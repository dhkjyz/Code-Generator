package com.mango.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mango.maker.meta.Meta;
import com.mango.maker.meta.enums.FileTypeEnum;
import com.mango.maker.meta.enums.StateTypeEnum;
import com.mango.maker.template.model.TemplateMakeConfig;
import com.mango.maker.template.model.TemplateMakeFileConfig;
import com.mango.maker.template.model.TemplateMakerModelConfig;
import com.mango.maker.template.model.TemplateMakerOutputConfig;
import com.mango.maker.template.utils.TemplateMakerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class TemplateMake {

    /**
     * 抽象出制作单个文件的方法
     *
     * @param modelConfig 主要是从该封装类中获取路径
     * @return
     */
    public static Meta.FileConfigDTO.FileInfo makeFileTemplate(TemplateMakerModelConfig modelConfig, String sourceRootPath, File inputFile, TemplateMakeFileConfig.FileFilterModel fileInfoConfig) {


        String fileOutPutAbsolutePath = inputFile.toString();
        //获取文件输出的绝对路径
        String fileInputAbsolutePath = fileOutPutAbsolutePath + ".ftl";

        //由于meta.json的文件配置是相对路径 ，还得把相对路径放在文件列表返回给上一层调用
        fileOutPutAbsolutePath = fileOutPutAbsolutePath.replace("\\", "/");
        String outputPath = fileOutPutAbsolutePath.replace(sourceRootPath + "/", "");
        String inputPath = outputPath + ".ftl";

        String fileContent = null;
        //如果已经有模版文件，说明不是第一次制作，则在模版基础上继续挖坑

        boolean hasTemplateFile = FileUtil.exist(fileInputAbsolutePath);
        if (hasTemplateFile) {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileOutPutAbsolutePath);
        }

        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = modelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : modelConfig.getModels()) {
            //不是分组
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                //是分组
                String groupKey = modelGroupConfig.getGroupKey();
                //注意挖坑要多一个层级
                replacement = String.format("${%s.%s}", groupKey, modelInfoConfig.getFieldName());
            }
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }


        Meta.FileConfigDTO.FileInfo fileInfo = new Meta.FileConfigDTO.FileInfo();
        fileInfo.setInputPath(inputPath);
        fileInfo.setOutputPath(outputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setGenerateType(StateTypeEnum.DYNAMIC.getValue());
        //是否更改了文件内容
        boolean contentEquals = newFileContent.equals(fileContent);
        if (!hasTemplateFile) {
            if (contentEquals) {
                //没有模版文件，没有更改内容的情况
                fileInfo.setInputPath(outputPath);
                fileInfo.setGenerateType(StateTypeEnum.STATIC.getValue());
            } else {
                //没有模版文件，更改内容的情况
                FileUtil.writeUtf8String(newFileContent, fileInputAbsolutePath);
            }
        } else if (!contentEquals) {
            //有模版文件，且内容改变，生成模版文件
            FileUtil.writeUtf8String(newFileContent, fileInputAbsolutePath);
        }
        return fileInfo;
    }

    /**
     * 制作文件模版
     *
     * @param fileConfig
     * @param modelConfig
     * @param sourceRootPath
     * @return
     */
    private static List<Meta.FileConfigDTO.FileInfo> makeFileTemplates(TemplateMakeFileConfig fileConfig, TemplateMakerModelConfig modelConfig, String sourceRootPath) {

        List<Meta.FileConfigDTO.FileInfo> newfileInfoList = new ArrayList<>();
        if (fileConfig == null) {
            return newfileInfoList;
        }

        List<TemplateMakeFileConfig.FileFilterModel> files = fileConfig.getFiles();

        if (files.isEmpty()) {
            return newfileInfoList;
        }


        //文件过滤:  读取文件过滤规则
        for (TemplateMakeFileConfig.FileFilterModel fileInfoOutofFilterd : files) {

            List<TemplateMakeFileConfig.FileFilterConfig> fileFilterConfig = fileInfoOutofFilterd.getFileFilterConfig(); //过滤规则
            String outPutPath = fileInfoOutofFilterd.getPath();
            String fileInfoWantToFilter = sourceRootPath + "/" + outPutPath; //准备要去过滤的File，可能是文件或则目录。

            //获得已经过滤好的文件列表
            List<File> filefiltered = FileFilter.doFilter(fileInfoWantToFilter, fileFilterConfig);

            //排除".ftl模版文件"
            List<File> execludeFTlFileList = filefiltered.stream().filter(fileInfo -> !"ftl".equals(FileUtil.getSuffix(fileInfo))).collect(Collectors.toList());

            for (File file : execludeFTlFileList) {
                //已过滤好的文件交给生成模板函数
                Meta.FileConfigDTO.FileInfo fileInfo = makeFileTemplate(modelConfig, sourceRootPath, file, fileInfoOutofFilterd);
                newfileInfoList.add(fileInfo);
            }
        }
        //如果是文件分组
        Meta.FileConfigDTO.FileInfo groupFileInfo = new Meta.FileConfigDTO.FileInfo();
        TemplateMakeFileConfig.FileGroupConfig fileGroupConfig = fileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            String groupName = fileGroupConfig.getGroupName();
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();

            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setCondition(condition);
            //把文件全放在一个分组内

            groupFileInfo.setFiles(newfileInfoList);
            newfileInfoList = new ArrayList<>();
            newfileInfoList.add(groupFileInfo);
        }
        return newfileInfoList;
    }


    /**
     * 获取模型信息列表
     *
     * @param modelConfig
     * @return
     */
    private static List<Meta.ModelConfigDTO.ModelInfo> getModelInfo(TemplateMakerModelConfig modelConfig) {
        //1. 初始化空的模型参数列表
        List<Meta.ModelConfigDTO.ModelInfo> newModelInfoList = new ArrayList<>();
        //2. 卫语句
        if (modelConfig == null) {
            return newModelInfoList;
        }
        List<TemplateMakerModelConfig.ModelInfoConfig> models = modelConfig.getModels();
        if (modelConfig.getModels().isEmpty()) {
            return newModelInfoList;
        }

        //3. 处理用户输入信息 并转换成Meta.json的数据
        List<Meta.ModelConfigDTO.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfigDTO.ModelInfo modelInfo = new Meta.ModelConfigDTO.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());

        //4. 处理模型参数组逻辑
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = modelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            Meta.ModelConfigDTO.ModelInfo modelGroupInfo = new Meta.ModelConfigDTO.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, modelGroupInfo);
            modelGroupInfo.setModels(inputModelInfoList);
            newModelInfoList.add(modelGroupInfo);
            return newModelInfoList;
        }
        newModelInfoList.addAll(inputModelInfoList);
        return newModelInfoList;
    }

    /**
     * 封装参数 重载方法主入口
     *
     * @param config
     * @return
     */
    public static long makeTemplate(TemplateMakeConfig config) {
        long id = config.getId();
        Meta meta = config.getMeta();
        String originProjectPath = config.getOriginProjectPath();
        TemplateMakeFileConfig fileConfig = config.getFileConfig();
        TemplateMakerModelConfig modelConfig = config.getModelConfig();
        TemplateMakerOutputConfig outputConfig = config.getOutputConfig();
        long l = makeTemplate(meta, originProjectPath, fileConfig, modelConfig, outputConfig, id);
        return l;
    }


    /**
     * 制作模版的主方法
     *
     * @param newMeta
     * @param originProjectPath
     * @param fileConfig        包含路径和过滤规则
     * @param modelConfig
     * @param id
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath,
                                    TemplateMakeFileConfig fileConfig,
                                    TemplateMakerModelConfig modelConfig,
                                    TemplateMakerOutputConfig outputConfig, Long id) {

        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }
        //复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id; //主目录
        //是否为首次制作模版
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(tempDirPath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }
        //一、输入信息
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();

        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");//注意win系统需要对路径进行转义

        //二、读取元信息，生成按照过滤规则，过滤好并要写入meta.json的文件列表项。
        List<Meta.FileConfigDTO.FileInfo> fileInfoList = makeFileTemplates(fileConfig, modelConfig, sourceRootPath);

        List<Meta.ModelConfigDTO.ModelInfo> newModelInfoList = getModelInfo(modelConfig);

        //五、生成配置文件
        String metaOutputPath = templatePath + File.separator + "meta.json";
        //如果已有meta文件，说明不是第一次制作，则meta的基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            //则从json文件读取到oldmeta对象
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            //获取列表， 向列表添加元素
            List<Meta.FileConfigDTO.FileInfo> filesInfoList = newMeta.getFileConfig().getFiles();
            filesInfoList.addAll(fileInfoList);

            List<Meta.ModelConfigDTO.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);
            //meta.json配置项去重
            newMeta.getFileConfig().setFiles(distinctFiles(filesInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));

        } else {
            Meta.FileConfigDTO fileConfigDTO = new Meta.FileConfigDTO();
            fileConfigDTO.setFiles(fileInfoList);
            newMeta.setFileConfig(fileConfigDTO);
            fileConfigDTO.setSourceRootPath(sourceRootPath);

            Meta.ModelConfigDTO modelConfigInJson = new Meta.ModelConfigDTO();
            newMeta.setModelConfig(modelConfigInJson);
            List<Meta.ModelConfigDTO.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfigInJson.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);
        }

        //追加调用工具类实现组内文件和组外去重功能
        if (outputConfig != null) {
            if (outputConfig.isRemoveGroupFilesFromRoot()) {
                List<Meta.FileConfigDTO.FileInfo> undofileInfoList = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(undofileInfoList));
            }
        }
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        return id;
    }


    /**
     * 对Meta.json文件中的fileConfig.files的信息进行去重处理
     *
     * @param fileInfoList
     * @return
     */
    public static List<Meta.FileConfigDTO.FileInfo> distinctFiles(List<Meta.FileConfigDTO.FileInfo> fileInfoList) {

        //完善多次制作模板解决同组合并问题。

        //1.有分组的，以组为单位划分
        Map<String, List<Meta.FileConfigDTO.FileInfo>> groupKeyFileInfoListMap = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.FileConfigDTO.FileInfo::getGroupKey));
        //2. 同组内的文件配置合并
        //保存每个组对应的合并后的对象map
        Map<String, Meta.FileConfigDTO.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.FileConfigDTO.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfigDTO.FileInfo> tempFileInfoList = entry.getValue();
            List<Meta.FileConfigDTO.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(
                            Collectors.toMap(
                                    Meta.FileConfigDTO.FileInfo::getInputPath, o -> o, (e, r) -> r
                            )).values());
            // 使用新的group配置
            Meta.FileConfigDTO.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }

        List<Meta.FileConfigDTO.FileInfo> resultList = new ArrayList<>(groupKeyMergedFileInfoMap.values());
        //将未分组的文件添加到结果列表
        List<Meta.FileConfigDTO.FileInfo> noGroupFileInfoList = fileInfoList.stream().filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey())).collect(Collectors.toList());

        resultList.addAll(new ArrayList<>(noGroupFileInfoList.stream().collect(
                Collectors.toMap(
                        Meta.FileConfigDTO.FileInfo::getInputPath, o -> o, (e, r) -> r)
        ).values()));
        return resultList;
    }

    /**
     * 对Meta.json文件中的modelConfig.models的信息进行去重处理
     *
     * @param modelInfoList
     * @return
     */
    public static List<Meta.ModelConfigDTO.ModelInfo> distinctModels(List<Meta.ModelConfigDTO.ModelInfo> modelInfoList) {
        //完善多次制作模板解决同组合并问题。

        //1.有分组的，以组为单位划分
        Map<String, List<Meta.ModelConfigDTO.ModelInfo>> groupKeyModelInfoListMap = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.groupingBy(Meta.ModelConfigDTO.ModelInfo::getGroupKey));
        //2. 同组内的文件配置合并
        //保存每个组对应的合并后的对象map
        Map<String, Meta.ModelConfigDTO.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>();
        for (Map.Entry<String, List<Meta.ModelConfigDTO.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfigDTO.ModelInfo> tempModelInfoList = entry.getValue();
            List<Meta.ModelConfigDTO.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfigDTO.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values());
            // 使用新的group配置
            Meta.ModelConfigDTO.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }

        List<Meta.ModelConfigDTO.ModelInfo> resultList = new ArrayList<>(groupKeyMergedModelInfoMap.values());
        //将未分组的文件添加到结果列表
        List<Meta.ModelConfigDTO.ModelInfo> noGroupModelInfoList = modelInfoList.stream().filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey())).collect(Collectors.toList());

        resultList.addAll(new ArrayList<>(noGroupModelInfoList.stream().collect(
                Collectors.toMap(Meta.ModelConfigDTO.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values()));
        return resultList;
    }

}
