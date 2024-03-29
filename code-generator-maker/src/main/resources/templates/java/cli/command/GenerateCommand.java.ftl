package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.model.DataModel;
import ${basePackage}.generator.MainFileGenerator;
import lombok.Data;
import picocli.CommandLine;
import java.util.concurrent.Callable;
<#--生成选项-->
<#macro generateOption indent modelInfo>
${indent}@CommandLine.Option(names = {"--${modelInfo.fieldName}"},description = "${modelInfo.description}",arity = "0..1",interactive = true,echo = true)
${indent}private ${modelInfo.type}  ${modelInfo.fieldName}=  <#if modelInfo.type=="boolean"> ${modelInfo.defaultValue};<#else>${modelInfo.defaultValue?c};</#if>
</#macro>
<#--生成命令调用-->
<#macro generateCommand indent modelInfo>
${indent}System.out.println("输入${modelInfo.groupName}配置:");
${indent}CommandLine ${modelInfo.groupKey}CommandLine= new CommandLine(${modelInfo.type}Command.class);
${indent}${modelInfo.groupKey}CommandLine.execute(${modelInfo.allArgsStr});
</#macro>

@Data
@CommandLine.Command (name = "generate", description="生成代码",mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

<#list modelConfig.models as modelInfo>
    <#--有分组-->
    <#if modelInfo.groupKey??>
    /**
    * ${modelInfo.description}
    */
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    <#--根据分组生成命令类-->
    @CommandLine.Command(name="${modelInfo.groupKey}")
    @Data
    public static class ${modelInfo.type}Command implements Runnable{
        <#list modelInfo.models as subModelInfo>
            <@generateOption modelInfo=subModelInfo indent="        "/>
        </#list>
        @Override
        public void run(){
        <#list modelInfo. models as subModelInfo >
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
        </#list>
        }
    }
    <#else>
        <@generateOption modelInfo=modelInfo indent="    "/>
    </#if>
</#list>
    <#--生成调用方法-->
    public Integer call() throws Exception  {
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condition??>
        if (${modelInfo.condition} ){
        <@generateCommand modelInfo=modelInfo indent="                "/>
        }
        <#else>
            <@generateCommand modelInfo=modelInfo indent="                "/>
        </#if>
        </#if>
        </#list>
        <#--填充数据模型对象-->
        DataModel dataModel =new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        <#list modelConfig.models as modelInfo>
            <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
            </#if>
        </#list>
        <#--生成代码-->
        MainFileGenerator.doGenerator(dataModel);
        return 0 ;
    }
}