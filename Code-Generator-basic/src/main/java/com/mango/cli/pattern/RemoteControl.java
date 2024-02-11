package com.mango.cli.pattern;

public class RemoteControl {
    private Command command;
    //想象成给遥控器添加按钮
    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        // 按下按钮，执行命令
        command.execute();
    }
}