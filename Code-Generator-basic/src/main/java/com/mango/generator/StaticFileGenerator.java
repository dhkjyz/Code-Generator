package com.mango.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 通过hutool与递归思想，静态生成，外层项目code-sample文件夹下acm-template的全部代码
 */
public class StaticFileGenerator {
    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir"); // code-generator-basic
        File projectParentPath = new File(projectPath).getParentFile(); // code-generator

        File inputPath = new File(projectParentPath, "code-sample/acm-template");
        String outputPath = projectPath;
        System.out.println(inputPath.getPath());
        System.out.println(outputPath);

        copyStaticFileByRecursive(inputPath.getPath(), outputPath);
        //copyStaticFileByHutool(inputPath,new File(outputPath));
    }

    /**
     * 使用hutool实现复制文件夹全部文件
     *
     * @param inputPath
     * @param outputPath
     */
    public static void copyStaticFileByHutool(File inputPath, File outputPath) {
        FileUtil.copy(inputPath, outputPath, false);
    }


    /**
     * 使用递归思想完成文件夹复制
     *
     * @param inputPath
     * @param outputPath
     */
    public static void copyStaticFileByRecursive(String inputPath, String outputPath) {

        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try {
            RecursiveFunction(inputFile, outputFile);
        } catch (Exception e) {
            System.err.println("文件复制失败 ");
            e.printStackTrace();
        }
    }

    public static void RecursiveFunction(File inputFile, File outPutFile) throws IOException {
        if (inputFile.isDirectory()) {
            File destOutputFile = new File(outPutFile, inputFile.getName());
            if (!destOutputFile.exists()) {
                destOutputFile.mkdir();
            }
            File[] files = inputFile.listFiles();
            if (ArrayUtil.isEmpty(files)) {
                return;
            }
            for (File file : files) {
                RecursiveFunction(file, destOutputFile);
            }
        }
        if (inputFile.isFile()) {
            /*
            这行代码创建了一个目标文件的路径（destPath），它将复制的文件复制到 outputFile 的路径中，并使用 inputFile 的文件名作为目标文件的名称。
            toPath() 方法将 outputFile 转换为 Path 对象，然后 resolve() 方法用于在此路径上附加 inputFile 的名称。
             */
            Path destPath = outPutFile.toPath().resolve(inputFile.getName());

            /*
            如果目标位置已经存在同名文件，StandardCopyOption.REPLACE_EXISTING 选项将替换目标文件。
             */

            Files.copy(inputFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

        }
    }
}
