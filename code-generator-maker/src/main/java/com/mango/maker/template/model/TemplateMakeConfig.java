package com.mango.maker.template.model;

import com.mango.maker.meta.Meta;
import lombok.Data;

/**
 * 配置的实体类（总）
 * 赋默认值防止NPE
 */

@Data
public class TemplateMakeConfig {
    /**
     * 生成模版的id，id保持一致，则可以保证在一个文件夹制作多次模版
     */
    private long id;

    /**
     * 模版的元数据,生成模版时，可能已经有了Meta数据
     */
    private Meta meta = new Meta();
    /**
     * 源码根路径
     */
    private String originProjectPath;
    /**
     * 文件配置 包括需要制作模版的文件路径与其过滤规则，文件分组
     */
    private TemplateMakeFileConfig fileConfig = new TemplateMakeFileConfig();
    /**
     * 模型配置 ，包括基本模型信息列表 ，分组配置
     */
    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();

}
