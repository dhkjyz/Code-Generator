package ${basePackage}.model;

import lombok.Data;

<#macro  generateModel indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent}* ${modelInfo.fieldName}
${indent}* ${modelInfo.description}
${indent}*/
</#if>
${indent}public ${modelInfo.type} ${modelInfo.fieldName} <#if modelInfo.dafaultValue??> = ${modelInfo.defaultValue?c}; <#else>; </#if>
</#macro>

/**
 *${author}
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>
    <#--有分组-->
    <#if modelInfo.groupKey??>
    /**
    *${modelInfo.groupName}
    */
    public ${modelInfo.type} ${modelInfo.groupKey}=new ${modelInfo.type}();

    /**
    * ${modelInfo.description}
    */
    @Data
    public static class ${modelInfo.type} {
        <#list modelInfo.models as modelInfo>
            <@generateModel modelInfo=modelInfo indent="        "/>
        </#list>
    }
<#else>
    <#--    无分组-->
    <@generateModel modelInfo=modelInfo indent="    "/>
    </#if>
</#list>
}
