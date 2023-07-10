package cn.hoyobot.example;

import cn.hoyobot.sdk.command.Command;
import cn.hoyobot.sdk.command.CommandSender;
import cn.hoyobot.sdk.command.CommandSettings;
import cn.hoyobot.sdk.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * 这是一个创建机器人命令的例子
 */
public class ExampleCommand extends Command {

    private static final CommandSettings.Builder commandSetting = (new CommandSettings.Builder()).put("example", "例子插件命令", new String[0]);

    public ExampleCommand(@NotNull String name) {
        super(name, commandSetting.build());
    }

    @Override
    public boolean onExecute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {

        //判断它是控制台执行还是米游社执行的命令
        //控制台执行
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("例子插件命令执行成功!");
        }

        return true;
    }
}
