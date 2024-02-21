package ${basePackage}.generator;
import ${basePackage}.generator.DynamicFileGenerator;
import ${basePackage}.generator.StaticFileGenerator;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;

/**
 * 动静结合，即先生成静态文件，再生成动态文件进行覆盖
 */
public class MainFileGenerator {


    public static void doGenerator(Object model) throws IOException, TemplateException {

        String inputRootPath= "D:\\code\\yupi-project\\Code-Generator\\code-sample\\acm-template-pro";
        String outRootPath= "D:\\code\\yupi-project\\Code-Generator\\acm-template-pro";

        String inputPath;
        String outputPath;

        <#list fileConfig.files as fileInfo>
        inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outRootPath,"${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "dynamic">
        DynamicFileGenerator.doGenerate(inputPath, outputPath, model);
        <#else>
        StaticFileGenerator.copyStaticFileByHutool(inputPath, outputPath);
        </#if>

        </#list>
    }

}
