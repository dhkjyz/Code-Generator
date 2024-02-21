package com.mango.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.mango.maker.generator.File.DynamicFileGenerator;
import com.mango.maker.generator.JarGenerator;
import com.mango.maker.generator.ScriptGenerator;
import com.mango.maker.meta.Meta;
import com.mango.maker.meta.MetaMessage;
import com.mango.maker.meta.MetaVolidate;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 编写测试程序，对已经写好模版的文件测试是否能在制定位置生成代码
 */
public abstract class GenerateTemplate
{
    public static void doGenerate() throws TemplateException, IOException, InterruptedException {
        //1、获取元信息
        Meta meta = getWholeMeta();
        //2、 定义输出路径
        String outputPath = initOutputPath(meta);

        // 3、读取resources目录
        String inputResourcePath = readResourcePath();

        //4、复制模版文件到相对路径
        copysourceFiles(meta);

        //5. 获取 Java包的工作目录
        String outputBaseJavaPackagePath = initpackagePath(meta, outputPath);

        //6.根据模版生成动态和静态的文件
        generatefile(meta, outputPath, inputResourcePath, outputBaseJavaPackagePath);

        //7.生成jar包
        generateJar();

        //8. 封装脚本
        generatorShellFile(meta, outputPath);
    }
    protected static Meta getWholeMeta() {
        //1.首先获取meta值
        Meta meta = MetaMessage.getMeta();
        MetaVolidate.doGenerate(meta);
        return meta;
    }

    protected static void generatorShellFile(Meta meta, String outputPath) throws IOException {
        String shellOutputFilePath = outputPath + File.separator + "generator";
        String jarName = String .format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target/" + jarName;
        ScriptGenerator.doGenerate(shellOutputFilePath,jarPath);
    }

    protected static void generateJar() throws IOException, InterruptedException {
        //运行jar包
        JarGenerator.doGenerate("D:\\code\\yupi-project\\Code-Generator\\code-generator-maker\\generated\\acm-template-pro-generator");
    }

    protected static void generatefile(Meta meta, String outputPath, String inputResourcePath, String outputBaseJavaPackagePath) throws IOException, TemplateException {
        String inputFilePath;
        String outputFilePath;

        // model.DataModel
        inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates/java/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        //cli.command.ConfigCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        //cli.command.ListCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        //cli.command.GenerateCommand
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        //cli.command.CommandExecute
        inputFilePath = inputResourcePath + File.separator + "templates/java/cli/CommandExecute.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/cli/CommandExecute.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        //generator.DynamicFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/file/DynamicFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/generator/DynamicFileGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        //generator.StaticFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/file/StaticFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/generator/StaticFileGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        //generator.MainFileGenerator
        inputFilePath = inputResourcePath + File.separator + "templates/java/generator/file/MainFileGenerator.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/generator/MainFileGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        //Main.java
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "/Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
        //README.md
        inputFilePath = inputResourcePath + File.separator + "templates/java/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }

    protected static String initpackagePath(Meta meta, String outputPath) {
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join("/", StrUtil.split(outputBasePackage, "."));
        // generated/acm-template-pro-generator/src/main/java/com/mango
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java" + File.separator + outputBasePackagePath;
        return outputBaseJavaPackagePath;
    }

    protected static void copysourceFiles(Meta meta) {
        String inputRelativepath = System.getProperty("user.dir")+ File.separator + meta.getFileConfig().getOutputRootPath()+File.separator+".source";

        String sourcePath = meta.getFileConfig().getSourceRootPath();

        FileUtil.copy( sourcePath,inputRelativepath, true);
    }

    protected static String readResourcePath() {
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();
        return inputResourcePath;
    }

    protected   static String initOutputPath(Meta meta) {
        // 输出的根路径
        String projectPath = System.getProperty("user.dir");//code-generator-maker
        String outputPath = projectPath + File.separator + meta.getFileConfig().getOutputRootPath() + File.separator + meta.getName();
        //注意不要遗漏必要的判空
        if(!FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }
        return outputPath;
    }


}
