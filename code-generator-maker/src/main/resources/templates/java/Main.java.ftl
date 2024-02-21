package ${basePackage};
import ${basePackage}.cli.CommandExecute;
public class Main {

    /**
     * 充当用户的角色
     */
    public static void main(String[] args) {
        CommandExecute commandExecute = new CommandExecute();
        commandExecute.doExecute(args);
    }
}
