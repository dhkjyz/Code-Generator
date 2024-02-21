package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.model.DataModel;
import ${basePackage}.generator.MainFileGenerator;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * 生成代码,接受参数并生成代码
 * 动态模板的数据模型：定义在第二期写好的MainTemplateData中，使用BeanUtil.copyProperties将接收的参数赋值给配置对象
 */
@Data
@CommandLine.Command (name = "generate", description="生成代码",mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    <#list modelConfig.models as modelInfo>
    @CommandLine.Option(names = {"-${modelInfo.abbr}","--${modelInfo.fieldName}"},description = "${modelInfo.description}",arity = "0..1",interactive = true,echo = true)
    private ${modelInfo.type}  ${modelInfo.fieldName};
    </#list>


    @Override
    public Integer call() throws Exception {

        DataModel dataModel =new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        MainFileGenerator.doGenerator(dataModel);
        System.out.println("配置信息：" + dataModel);
        return 0;
    }
}