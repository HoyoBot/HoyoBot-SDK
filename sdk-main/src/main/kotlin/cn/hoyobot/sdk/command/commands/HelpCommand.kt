package cn.hoyobot.sdk.command.commands

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.Command
import cn.hoyobot.sdk.command.CommandSender
import cn.hoyobot.sdk.command.CommandSettings
import cn.hoyobot.sdk.command.ConsoleCommandSender

class HelpCommand : Command("help", CommandSettings.builder().put("/help <命令>", "查看命令列表或帮助", arrayOf("?")).build()) {
    override fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean {

        if (sender !is ConsoleCommandSender) return true

        if (args.isEmpty()) this.showHelpList(sender)
        else {
            val targetCommand = args[0]
            if (!HoyoBot.instance.getCommandMap().commands.containsKey(targetCommand)) {
                sender.sendMessage("你查询的命令${targetCommand}不存在!")
                return true
            }
            val command = HoyoBot.instance.getCommandMap().commands[targetCommand]!!
            val builder = StringBuilder("帮助: $targetCommand").append("描述: ${command.description}")
                .append("用法: ${command.usageMessage}")
            sender.sendMessage(builder.toString().trim())
        }

        return true
    }

    private fun showHelpList(sender: CommandSender) {
        val builder = StringBuilder("当前机器人总共有${HoyoBot.instance.getCommandMap().commands.size}条命令").append("\n")
        HoyoBot.instance.getCommandMap().commands.forEach { (_, command) ->
            run {
                builder.append(command.name).append(" - ").append(command.description).append(" 用法:")
                    .append(command.usageMessage).append("\n")
            }
        }
        sender.sendMessage(builder.toString().trim())
    }

}