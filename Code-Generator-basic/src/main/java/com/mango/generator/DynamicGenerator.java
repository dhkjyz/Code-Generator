package com.mango.generator;

import com.mango.freemaker.dataModel.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;

/**
 * 根据freemaker动态生成单个文件
 */
public class DynamicGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        //模版文件存放位置 在src/main/resources/templates
        String path = System.getProperty("user.dir"); //
        String inputPath = path + File.separator + "src/main/resources/templates/template.java.ftl";
        String outputPath = path + File.separator + "MainTemplate.java";
        MainTemplateConfig dataObj = new MainTemplateConfig();
        dataObj.setAuthor("Mango");
        dataObj.setLoop(true);
        dataObj.setMessage("SUM:");
        doGenerate(inputPath, outputPath, dataObj);
    }

    /**
     * @param inputPath  模版文件存放位置
     * @param outputPath 将生成文件的地址
     * @param dataobj
     */
    public static void doGenerate(String inputPath, String outputPath, Object dataobj) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31); // 创建 FreeMarker 配置对象
        //设置模板文件所在的路径 
        File templateDir = new File(inputPath).getParentFile();
        cfg.setDirectoryForTemplateLoading(templateDir);
        //加载模板文件
        String templateName = new File(inputPath).getName();
        Template template = cfg.getTemplate(templateName);
        PrintWriter printWriter = new PrintWriter(outputPath);
        template.process(dataobj, printWriter);
        printWriter.close();
    }
}
