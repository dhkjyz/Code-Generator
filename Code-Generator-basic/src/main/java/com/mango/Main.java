package com.mango;

import com.mango.cli.CommandExecutor;

/**
 * 充当用户的角色
 */
public class Main {
    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        //args = new String[]{"generate", "-l", "-a", "-m" };
        //args = new String[]{"config"};
//        args = new String[]{"list"};
        commandExecutor.doExecute(args);

    }
}
