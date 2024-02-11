package com.mango.generator;

import com.mango.freemaker.dataModel.MainTemplateData;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Paths.*;

/**
 * 根据freemaker动态生成单个文件
 */
public class DynamicGenerator {

    public static void main(String[] args) throws TemplateException, IOException {
        //模版文件存放位置 在src/main/resources/templates
        String path = System.getProperty("user.dir"); //
        String inputPath = path + File.separator + "src/main/resources/templates/template.java.ftl";
        String outputPath = path + File.separator + "MainTemplate.java";
        //准备数据对象
        MainTemplateData dataObj = new MainTemplateData();
        dataObj.setAuthor("Mango");
        dataObj.setLoop(true);
        dataObj.setMessage("SUM:");
        //调用生成文件的方法
        doGenerate(inputPath, outputPath, dataObj);
    }

    /**
     * 用到了freemaker
     * @param inputPath  模版文件存放位置
     * @param outputPath 将生成文件的地址
     * @param dataobj
     */
    public static void doGenerate(String inputPath, String outputPath, Object dataobj) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31); // 创建 FreeMarker 配置对象
        //设置模板文件所在的路径 
        File templateDir = new File(inputPath).getParentFile();
        cfg.setDirectoryForTemplateLoading(templateDir);
        cfg.setDefaultEncoding("utf-8");
        //加载模板文件
        String templateName = new File(inputPath).getName();
        Template template = cfg.getTemplate(templateName,"utf-8");
        /**
         * 这个函数的功能是创建一个BufferedWriter对象，用于向指定的输出路径写入文本内容。具体来说，它使用Files.newOutputStream()方法创建一个输出流
         * 并将其包装在一个OutputStreamWriter对象中，该对象使用UTF-8编码。最后，将这个OutputStreamWriter对象传递给BufferedWriter构造函数，以创建一个缓冲写入器。
         */
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(get(outputPath)), StandardCharsets.UTF_8));
        template.process(dataobj, out);
        out.close();
    }
}
