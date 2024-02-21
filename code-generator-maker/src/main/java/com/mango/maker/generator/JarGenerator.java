package com.mango.maker.generator;

import java.io.*;

public class JarGenerator {
    public static void doGenerate(String projectDir) throws IOException,InterruptedException {
        //清理之前的构建并打包
        //注意不同操作系统，执行的命令不同
        String winMavenCommand= "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand= "mvn clean package -DskipTests=true";
        String mavenCommand=winMavenCommand;
        //这里使用拆分的意图
        ProcessBuilder processBuilder=new ProcessBuilder(mavenCommand.split(" "));
        //设置进程的执行目录为projectDir
        processBuilder.directory(new File(projectDir));

        Process process =processBuilder.start();

        //读取命令的输出
        InputStream inputStream= process.getInputStream();
        BufferedReader reader= new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while((line= reader.readLine())!=null){
            System.out.println(line);
        }
        int exitCode= process.waitFor();
        System.out.println("命令执行结束，退出码："+exitCode);
    }

    public static void main(String[ ] args) throws IOException, InterruptedException {
        doGenerate("D:\\code\\yupi-project\\Code-Generator\\code-generator-maker\\generated\\acm-template-pro-generator");
    }


}
