package com.mango.maker;

import com.mango.maker.generator.main.GenerateTemplate;
import com.mango.maker.generator.main.ZipGenerator;

public class Main {
    public static void main(String[] args) throws Exception {
        //GenerateTemplate generateTemplate = new MainGenerator();
        GenerateTemplate generateTemplate = new ZipGenerator();
        generateTemplate.doGenerate();
    }
}
