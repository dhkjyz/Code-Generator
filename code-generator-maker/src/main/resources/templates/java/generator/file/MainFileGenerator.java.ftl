package ${basePackage}.generator;
import ${basePackage}.generator.DynamicFileGenerator;
import ${basePackage}.generator.StaticFileGenerator;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.IOException;
import ${basePackage}.model.DataModel;

<#macro generate indent fileInfo >
${indent}inputPath = new File(inputRootPath,"${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outRootPath,"${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "dynamic">
${indent}DynamicFileGenerator.doGenerate(inputPath, outputPath, model);
<#else>
${indent}StaticFileGenerator.copyStaticFileByHutool(inputPath, outputPath);
</#if>
</#macro>
public class MainFileGenerator {
    public static void doGenerator(DataModel model) throws IOException, TemplateException {

        String path = System.getProperty("user.dir");
        String inputRootPath=  path +File.separator + "generated"+ File.separator + "${fileConfig.inputRootPath}";
        String outRootPath= "D:\\code\\yupi-project\\Code-Generator\\acm-template-pro";

        String inputPath;
        String outputPath;
<#list modelConfig.models as modelInfo>
    <#-- 有分组-->
    <#if modelInfo.groupKey?? >
        <#list modelInfo.models as submodelInfo>
        ${submodelInfo.type} ${submodelInfo.fieldName} = model.${modelInfo.groupKey}.${submodelInfo.fieldName};

        </#list>
    <#else>
    <#-- 无分组-->
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};

    </#if>
</#list>
<#list fileConfig.files as fileInfo>
    <#if fileInfo.groupKey??>
        //groupKey = ${fileInfo.groupKey}
        <#if fileInfo.condition??>
        if(${fileInfo.condition}){
            <#list fileInfo.files as fileInfo>
                 <@generate fileInfo=fileInfo indent="             "/>
            </#list>
        }

        <#else>
            <#list fileInfo.files as fileInfo>
            <@generate fileInfo=fileInfo indent="      "/>

            </#list>
        </#if>
    <#else>
        <#if fileInfo.condition??>
        if(${fileInfo.condition}){
             <@generate fileInfo=fileInfo indent="              "/>
        }

        <#else>
                <@generate fileInfo=fileInfo indent="        "/>

        </#if>
    </#if>
</#list>
    }
}
