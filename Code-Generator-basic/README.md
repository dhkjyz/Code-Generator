# 第一阶段：开发一个基于命令行的ACM模版代码生成器 
>    技术点 ： picocli命令行开发框架 + 设计模式之命令模式 +  freemaker模版引擎



# 目录结构： 
1. ./acm-template 生成文件所在

2. 源码包分析 
   1. cli包

      -   command包
          -   ConfigCommand.java：运用picocli技术，通过命令行输入config，在终端输出允许用户传入的动态参数的信息,即可以理解模版文件挖空的文件。 
          -   GenerateCommand.java：运用picocli技术，通过命令行输入generate，并接收用户与模版文件交互的参数，最终在终端输出日志信息并在项目根目录下生成acm-template代码。
          -   ListCommand.java： 运用picocli技术，通过命令行输入list，遍历输出acm-template目录下的文件。
      -   example包：学习picocli官网的一些小案例，与主要逻辑无关。

      -   pattern包：学习命令模式的案例，与主要逻辑无关

      -   utils工具包：
          -   OptionUtil.java: 校验命令行输入的参数是否缺少模版所需要的。

   2. CommandExecutor.java文件：这里充当命令模式的遥控器。

       ```java
       	@Command(name = "mangoGenerator", mixinStandardHelpOptions = true)
       public class CommandExecutor implements Callable {
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
       ```

   2.   freemaker包
        -   dataModel包
            -   MainTemplateData：主要逻辑中模版文件所挖的“坑”，数据结构在这里定义。

   3.   generator包

        -   DynamicGenerator.java：基于freemaker技术，基于模版+数据=项目的设计，完成了对java文件动态生成，即由用户在命令行定义作者，输出信息，以及是否开启循环。

        -   StaticFileGenerator.java：基于hutool的FileUtils.copy实现目录整体静态拷贝，基于递归思想借助hutool实现目录静态拷贝

        -   MainGenerator.java：实现动态+静态结合对acm-template目录进行拷贝，先使用StaticFileGenerator.java中定义的静态方法拷贝整个目录，再使用DynamicGenerator.java的静态方法生成文件对需要修改的文件进行覆盖。

   4.   Main.java：程序的入口，在命令模式中充当用户的角色，调用遥控器即CommandExecutor类的静态方法，从而实现已定义的操作 。