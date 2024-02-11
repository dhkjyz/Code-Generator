package com.mango.cli.pattern;

public class TurnOffCommand implements Command {
    //给命令绑定一个设备。
    private Device device;

    public TurnOffCommand(Device device){
        this.device = device;
    }

    //调用设备的方法
    public void execute(){
        device.turnOff();
    }
}