package com.mango.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mango.maker.meta.Meta;
import com.mango.maker.meta.enums.FileTypeEnum;
import com.mango.maker.meta.enums.StateTypeEnum;
import com.mango.maker.template.model.FileFilterConfig;
import com.mango.maker.template.model.TemplateMakeFileConfig;
import com.mango.maker.template.model.enums.FileRegularFilterEnum;
import com.mango.maker.template.model.enums.FileTypeFilterEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class TemplateMake {

    /**
     * 抽象出制作单个文件的方法
     *
     * @param modelInfo
     * @param searchStr
     * @param sourceRootPath
     * @param inputFile
     * @return
     */
    public static Meta.FileConfigDTO.FileInfo makeFileTemplate(Meta.ModelConfigDTO.ModelInfo modelInfo, String searchStr, String sourceRootPath, File inputFile) {
        //获取文件输出的绝对路径
        String fileInputAbsolutePath = inputFile.toString();

        String fileOutPutAbsolutePath = fileInputAbsolutePath + ".ftl";

        //由于meta.json的文件配置是相对路径 ，还得把相对路径放在文件列表返回给上一层调用

       fileInputAbsolutePath =  fileInputAbsolutePath.replace("\\","/");
        String inputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String outputPath = inputPath + ".ftl";

        String fileContent = null;
        if (FileUtil.exist(fileOutPutAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutPutAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        //读取到原来文件内容，把searchStr 替换成 目标fieldName;
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);
        FileUtil.writeUtf8String(newFileContent, fileOutPutAbsolutePath);

        Meta.FileConfigDTO.FileInfo fileInfo = new Meta.FileConfigDTO.FileInfo();
        fileInfo.setInputPath(inputPath);
        fileInfo.setOutputPath(outputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(StateTypeEnum.DYNAMIC.getValue());

        return fileInfo;
    }




    /**
     * @param newMeta
     * @param originProjectPath
     * @param fileConfig 包含路径和过滤规则
     * @param modelInfo
     * @param searchStr
     * @param id
     */
    public static long makeTemplate(Meta newMeta, String originProjectPath, TemplateMakeFileConfig fileConfig, Meta.ModelConfigDTO.ModelInfo modelInfo, String searchStr, Long id) {

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
        List<TemplateMakeFileConfig.FileFilterModel> fileFilterModel = fileConfig.getFileFilterModel();
        for (TemplateMakeFileConfig.FileFilterModel filterModel : fileFilterModel) {
            //文件过滤getter
            List<FileFilterConfig> fileFilterConfig = filterModel.getFileFilterConfig();
            String inputFilePath = filterModel.getPath();
            String inputFileAbsolutePath= sourceRootPath + "/" + inputFilePath;

//            inputFileAbsolutePath= inputFileAbsolutePath.replace("\\\\","/");
            //获得已经过滤好的文件列表
            List<File> filefiltered = FileFilter.doFilter(inputFileAbsolutePath, fileFilterConfig);
            for (File file : filefiltered) {
                //已过滤好的文件交给生成模板函数
                Meta.FileConfigDTO.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, file);
                fileInfoList.add(fileInfo);
            }

        }

        //三、生成配置文件
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
            modelInfoList.add(modelInfo);
            //meta.json配置项去重
            newMeta.getFileConfig().setFiles(distinctFiles(filesInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));

        } else {
            Meta.FileConfigDTO fileConfigDTO = new Meta.FileConfigDTO();
            fileConfigDTO.setFiles(fileInfoList);
            newMeta.setFileConfig(fileConfigDTO);
            fileConfigDTO.setSourceRootPath(sourceRootPath);

            Meta.ModelConfigDTO modelConfig = new Meta.ModelConfigDTO();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfigDTO.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);
        }
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        return id;
    }


    /**
     * 对Meta.json文件中的fileConfig.files的信息进行去重处理
     * @param fileInfoList
     * @return
     */
    public static List<Meta.FileConfigDTO.FileInfo> distinctFiles(List<Meta.FileConfigDTO.FileInfo> fileInfoList) {
        List<Meta.FileConfigDTO.FileInfo> newFileInfoList = new ArrayList<>(fileInfoList.stream().collect(Collectors.toMap(Meta.FileConfigDTO.FileInfo::getInputPath, o -> o, (e, r) -> r)).values());
        return newFileInfoList;
    }

    /**
     * 对Meta.json文件中的modelConfig.models的信息进行去重处理
     *
     * @param modelInfoList
     * @return
     */
    public static List<Meta.ModelConfigDTO.ModelInfo> distinctModels(List<Meta.ModelConfigDTO.ModelInfo> modelInfoList) {
        List<Meta.ModelConfigDTO.ModelInfo> newModelInfoList = new ArrayList<>(modelInfoList.stream().collect(Collectors.toMap(Meta.ModelConfigDTO.ModelInfo::getFieldName, o -> o, (e, r) -> r)).values());
        return newModelInfoList;
    }

    public static void main(String[] args) {
        Meta meta = new Meta();
//        meta.setName("acm-template-generator");
//        meta.setDescription("ACM示例模版生成器");

        meta.setName("springboot-template");
        meta.setDescription("springboot项目模版生成器");


        String projectPath = System.getProperty("user.dir");
        String parentProjectPath = new File(projectPath).getParent();
        String originProjectPath = parentProjectPath + File.separator + "code-sample/springboot-init";

        File inputFilePath = new File("src/main/java/com/yupi/springbootinit");

        //todo:如何实现一些指定的文件夹或文件不生成模板？
        //文件过滤配置类
        TemplateMakeFileConfig templateMakeFileConfig = new TemplateMakeFileConfig();
        TemplateMakeFileConfig.FileFilterModel fileFilterModel = new TemplateMakeFileConfig.FileFilterModel();
        fileFilterModel.setPath("src/main/java/com/yupi/springbootinit/common");
        FileFilterConfig fileFilterConfig = FileFilterConfig
                .builder()
                .range(FileTypeFilterEnum.FILE_NAME.getValue())
                .rule(FileRegularFilterEnum.START_WITH.getValue())
                .value("Base")
                .build();
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        fileFilterConfigList.add(fileFilterConfig);
        fileFilterModel.setFileFilterConfig(fileFilterConfigList);

        //包含文件路径 文件过滤规则的参数 代替 inputFilePath
        List<TemplateMakeFileConfig.FileFilterModel> fileFilterModelList = new ArrayList<>();
        fileFilterModelList.add(fileFilterModel);

        templateMakeFileConfig.setFileFilterModel(fileFilterModelList);


        //模版参数信息
        Meta.ModelConfigDTO.ModelInfo modelInfo = new Meta.ModelConfigDTO.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        //替换变量
        String searchStr = "MainTemplate";
        long myId = IdUtil.getSnowflakeNextId();
        long id = makeTemplate(meta, originProjectPath, templateMakeFileConfig, modelInfo, searchStr, myId);
        System.out.println(id);


    }
}
