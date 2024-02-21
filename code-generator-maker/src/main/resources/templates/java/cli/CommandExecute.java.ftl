package ${basePackage}.cli;

import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * 充当遥控器
 */
@Command(name = "${name}", mixinStandardHelpOptions = true)
public class CommandExecute implements Callable {
    private final CommandLine commandLine;
    {
        commandLine=new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public Object call() throws Exception {
        System.out.println("请输入具体命令，或者输入 --help查看命令提示");
        return 0;
    }

    /**
     * 执行命令
     * @param args
     * @return
     */
    public  Integer doExecute(String[] args){
        return commandLine.execute(args);
    }
}
