package com.mango.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.mango.freemaker.dataModel.MainTemplateData;
import com.mango.generator.MainGenerator;
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

    //接受参数
    @CommandLine.Option(names = {"-l","-loop"},description = "循环开关",arity = "0..1",interactive = true,echo = true)
    private boolean loop;

    @CommandLine.Option(names = {"-a","-author"},description = "作者",arity = "0..1",interactive = true,echo = true)
    private String author ;

    @CommandLine.Option(names = {"-m","-message"},description = "输出信息",arity = "0..1",interactive = true,echo = true)
    private String message;


    @Override
    public Integer call() throws Exception {

        MainTemplateData mainTemplateData=new MainTemplateData();
        BeanUtil.copyProperties(this,mainTemplateData);
        MainGenerator.doGenerator(mainTemplateData);
        System.out.println("配置信息：" + mainTemplateData);
        return 0;
    }
}
