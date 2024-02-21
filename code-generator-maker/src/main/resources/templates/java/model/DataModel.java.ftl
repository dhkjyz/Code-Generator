package ${basePackage}.model;

import lombok.Data;

/**
 *${author}
 */
@Data
public class DataModel {

    <#list modelConfig.models as modelInfo>
        <#if modelInfo.description?exists>
            /**
            * ${modelInfo.fieldName}
            * ${modelInfo.description}
            */
       </#if>
    private ${modelInfo.type}  ${modelInfo.fieldName} <#if modelInfo.defaultValue?exists > = <#if modelInfo.type=="boolean">${modelInfo.defaultValue} ;<#else>${modelInfo.defaultValue?c}; </#if>  <#else>; </#if>
    </#list>
}
