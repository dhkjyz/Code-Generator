package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;

/**
 * 生成静态文件
 */
public class StaticFileGenerator {
    /**
     * 使用hutool拷贝目录，生成静态文件
     * @param inputPath 输入目录
     * @param outputPath 输出目录
     */
    public static void copyStaticFileByHutool(String  inputPath, String outputPath) {
        FileUtil.copy(inputPath, outputPath, false);
    }
}
