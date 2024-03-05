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
import com.mango.maker.template.model.FileFilterConfig;
import com.mango.maker.template.model.TemplateMakeFileConfig;
import com.mango.maker.template.model.TemplateMakerModelConfig;
import com.mango.maker.template.model.enums.FileRegularFilterEnum;
import com.mango.maker.template.model.enums.FileTypeFilterEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
public class TemplateMake {

    /**
     * 抽象出制作单个文件的方法
     *
     * @param modelConfig
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    public static Meta.FileConfigDTO.FileInfo makeFileTemplate(TemplateMakerModelConfig modelConfig, String sourceRootPath, File inputFile) {
        //获取文件输出的绝对路径
        String fileInputAbsolutePath = inputFile.toString();

        String fileOutPutAbsolutePath = fileInputAbsolutePath + ".ftl";

        //由于meta.json的文件配置是相对路径 ，还得把相对路径放在文件列表返回给上一层调用
        fileInputAbsolutePath = fileInputAbsolutePath.replace("\\", "/");
        String inputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String outputPath = inputPath + ".ftl";

        String fileContent = null;
        if (FileUtil.exist(fileOutPutAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutPutAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        //支持多个模型 ：对同一个文件的内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = modelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : modelConfig.getModels()) {
            //不是分组
            if (modelGroupConfig==null){
                replacement= String.format("${%s}",modelInfoConfig.getReplaceText());
            }else{
                //是分组
                String groupKey = modelGroupConfig.getGroupKey();
                //注意挖坑要多一个层级
                replacement = String.format("${%s.%s}",groupKey,modelInfoConfig.getFieldName());
            }
            newFileContent = StrUtil.replace(newFileContent,modelInfoConfig.getReplaceText(),replacement);
        }

        FileUtil.writeUtf8String(newFileContent, fileOutPutAbsolutePath);

        Meta.FileConfigDTO.FileInfo fileInfo = new Meta.FileConfigDTO.FileInfo();
        fileInfo.setInputPath(inputPath);
        fileInfo.setOutputPath(outputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(StateTypeEnum.DYNAMIC.getValue());
        return fileInfo;
    }


    /**
     *
     * @param newMeta
     * @param originProjectPath
     * @param fileConfig        包含路径和过滤规则
     * @param modelConfig
     * @param id
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakeFileConfig fileConfig, TemplateMakerModelConfig modelConfig, Long id) {

        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        //复制目录
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;
        //是否为首次制作模版
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(tempDirPath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }
        //一、输入信息
        //输入文件信息
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        System.out.println("sourceRootPath ： " + sourceRootPath);
        //注意win系统需要对路径进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        //二、制作文件
        List<Meta.FileConfigDTO.FileInfo> fileInfoList = new ArrayList<>();
        //得到具体的文件过滤信息
        List<TemplateMakeFileConfig.FileFilterModel> fileFilterModel = fileConfig.getFiles();
        //文件过滤后生成模板
        for (TemplateMakeFileConfig.FileFilterModel filterModel : fileFilterModel) {
            //文件过滤getter
            List<FileFilterConfig> fileFilterConfig = filterModel.getFileFilterConfig();
            String inputFilePath = filterModel.getPath();
            String inputFileAbsolutePath = sourceRootPath + "/" + inputFilePath;

//            inputFileAbsolutePath= inputFileAbsolutePath.replace("\\\\","/");
            //获得已经过滤好的文件列表
            List<File> filefiltered = FileFilter.doFilter(inputFileAbsolutePath, fileFilterConfig);

            for (File file : filefiltered) {
                //已过滤好的文件交给生成模板函数
                Meta.FileConfigDTO.FileInfo fileInfo = makeFileTemplate(modelConfig, sourceRootPath, file);
                fileInfoList.add(fileInfo);
            }
        }

        //四、实现文件分组
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
            groupFileInfo.setFiles(fileInfoList);
            fileInfoList = new ArrayList<>();
            fileInfoList.add(groupFileInfo);
        }

        //处理模型信息 目的就是能够读取到该对象，并转换成Meta.json的数据
        List<TemplateMakerModelConfig.ModelInfoConfig> models = modelConfig.getModels();
        //转换为配置接受的modelInfo对象
        List<Meta.ModelConfigDTO.ModelInfo> inputModelInfoList = models.stream().map(modelInfoConfig -> {
            Meta.ModelConfigDTO.ModelInfo modelInfo = new Meta.ModelConfigDTO.ModelInfo();
            BeanUtil.copyProperties(modelInfoConfig, modelInfo);
            return modelInfo;
        }).collect(Collectors.toList());

        List<Meta.ModelConfigDTO.ModelInfo> newModelInfoList = new ArrayList<>();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = modelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            String groupName = modelGroupConfig.getGroupName();
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();

            Meta.ModelConfigDTO.ModelInfo modelInfo = new Meta.ModelConfigDTO.ModelInfo();
            modelInfo.setGroupKey(groupKey);
            modelInfo.setGroupName(groupName);
            modelInfo.setCondition(condition);
            modelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(modelInfo);
        }
        newModelInfoList. addAll(inputModelInfoList);


        //五、生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";
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

    public static void main(String[] args) {
        Meta meta = new Meta();
        meta.setName("springboot-template");
        meta.setDescription("springboot项目模版生成器");


        String projectPath = System.getProperty("user.dir");
        String parentProjectPath = new File(projectPath).getParent();
        String originProjectPath = parentProjectPath + File.separator + "code-sample/springboot-init";

        String inputFilePath1 = "src/main/java/com/yupi/springbootinit/common";
        String inputFilePath2 = "src/main/resources/application.yml";

        //模版参数信息
        Meta.ModelConfigDTO.ModelInfo modelInfo = new Meta.ModelConfigDTO.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        //二、文件测试
        //初始化对象
        TemplateMakeFileConfig templateMakeFileConfig = new TemplateMakeFileConfig();
        List<TemplateMakeFileConfig.FileFilterModel> fileFilterModelList = new ArrayList<>();
        TemplateMakeFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakeFileConfig.FileGroupConfig();

        //定义文件过滤规则的参数
        TemplateMakeFileConfig.FileFilterModel fileFilterModel1 = new TemplateMakeFileConfig.FileFilterModel();
        TemplateMakeFileConfig.FileFilterModel fileFilterModel2 = new TemplateMakeFileConfig.FileFilterModel();
        //给文件过滤模型 赋值
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder().range(FileTypeFilterEnum.FILE_NAME.getValue()).rule(FileRegularFilterEnum.START_WITH.getValue()).value("Base").build();
        fileFilterConfigList.add(fileFilterConfig);

        fileFilterModel1.setPath(inputFilePath1);
        fileFilterModel1.setFileFilterConfig(fileFilterConfigList);

        fileFilterModel2.setPath(inputFilePath2);
        fileFilterModelList.add(fileFilterModel1);
        //文件分组赋值
        fileGroupConfig.setGroupKey("test1");
        fileGroupConfig.setGroupName("测试b");
        fileGroupConfig.setCondition("loop");
        //给文件配置类赋值
        templateMakeFileConfig.setFiles(Arrays.asList(fileFilterModel1, fileFilterModel2));
        templateMakeFileConfig.setFileGroupConfig(fileGroupConfig);

        //模型测试
        //初始化对象
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = new ArrayList<>();
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        //模型赋值
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库分组");


        TemplateMakerModelConfig.ModelInfoConfig modelConfigInfo1 = TemplateMakerModelConfig.ModelInfoConfig.builder().fieldName("url").type("String").defaultValue("jdbc:mysql://localhost:3306/my_db").replaceText("jdbc:mysql://localhost:3306/my_db").build();
        TemplateMakerModelConfig.ModelInfoConfig modelConfigInfo2 = TemplateMakerModelConfig.ModelInfoConfig.builder().fieldName("username").type("String").defaultValue("root").replaceText("root").build();
        modelInfoConfigList.addAll(Arrays.asList(modelConfigInfo1, modelConfigInfo2));
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
        templateMakerModelConfig.setModels(modelInfoConfigList);



        //替换变量

        long id = makeTemplate(meta, originProjectPath, templateMakeFileConfig, templateMakerModelConfig, 1764179463425376256l);

    }
}
