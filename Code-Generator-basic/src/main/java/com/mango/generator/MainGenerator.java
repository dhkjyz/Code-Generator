package com.mango.generator;

import com.mango.freemaker.dataModel.MainTemplateData;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 动静结合，即先生成静态文件，再生成动态文件进行覆盖
 */
public class MainGenerator {


    //后续这个projectName应该是可变的
    private static String projectName = "acm-template";

    public static void doGenerator(Object dataObj) throws IOException, TemplateException {

        //todo:优化点 不同操作系统适配问题 。想法：上linux时通过脚本进行替换 。
        //整个项目的根路径
        String path = System.getProperty("user.dir");
        File parentPath = new File(path).getParentFile();
        String inputPath = new File(parentPath, "code-sample/acm-template").getAbsolutePath();
        String outputPath = path;
        //生成静态文件
        StaticFileGenerator.copyStaticFileByRecursive(inputPath, outputPath);
        //生成动态文件
        String inputDynamicTemplatePath = path + File.separator   + "src/main/resources/templates/template.java.ftl";
        String outputDynamicFilePath = outputPath + File.separator + projectName + "/src/com/yupi/acm/MainTemplate.java";
        DynamicGenerator.doGenerate(inputDynamicTemplatePath, outputDynamicFilePath, dataObj);
    }
    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateData dataObj = new MainTemplateData();
        dataObj.setAuthor("Mango");
        dataObj.setLoop(true);
        dataObj.setMessage("求和结果 ：");
        doGenerator(dataObj);
    }

}
