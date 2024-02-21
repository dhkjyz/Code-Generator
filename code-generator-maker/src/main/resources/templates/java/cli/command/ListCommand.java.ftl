package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

//遍历输出要生成的文件。
@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
public class ListCommand  implements  Runnable{

    @Override
    public void run() {
        // 当前打开的窗口
        String projectPath = System.getProperty("user.dir");
        // 父级目录，项目根目录
        File parentFile = new File(projectPath).getParentFile();
        // 输入路径
        String inputPath = "${fileConfig.inputRootPath}";
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            // 打印文件信息
            System.out.println(file);
        }
    }
}