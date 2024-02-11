package com.mango.cli.example;

import com.mango.cli.utils.OptionUtil;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

class Login implements Callable<Integer> {
    @Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @Option(names = {"-p", "--password"},arity = "0..1", description = "Passphrase", interactive = true)
    String password;

    //默认情况下，是无法给交互式参数指定参数的，只能通过交互式来输入,
    // 解决方案 ：@Option注解中的arity属性来指定每个选项可接收的参数个数
    @Option(names = {"-cp", "--checkPassword"},arity = "0..1", description = "Check Password", interactive = true)
    String checkPassword;

    public Integer call() throws Exception {
        // 打印出密码
        System.out.println("密码是：" + password);
        System.out.println("确认密码是：" + checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        args= new String[]{"-u", "user123", "-p"};
        new CommandLine(new Login()).execute(OptionUtil.processInteractiveOptions(Login.class, args));
    }
}