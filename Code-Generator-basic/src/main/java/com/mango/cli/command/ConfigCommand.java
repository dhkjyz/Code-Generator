package com.mango.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.mango.freemaker.dataModel.MainTemplateData;
import picocli.CommandLine;

import java.lang.reflect.Field;

//作用：输出允许用户传入的动态参数的信息，本项目中的 MainTemplateConfig类 的字段信息
@CommandLine.Command(name = "config", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {
    @Override
    public void run() {
        //使用hutool下的反射工具库获取字段
        Field[] fields = ReflectUtil.getFields(MainTemplateData.class);
        for (Field field : fields) {
            System.out.println(field.getName()+"你好 ");
            System.out.println(field.getType());
        }
    }
}
