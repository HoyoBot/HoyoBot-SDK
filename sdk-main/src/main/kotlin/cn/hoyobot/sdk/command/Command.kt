package cn.hoyobot.sdk.command

import cn.hoyobot.sdk.command.data.CommandData
import cn.hoyobot.sdk.command.data.CommandEnumData
import cn.hoyobot.sdk.command.data.CommandParam
import cn.hoyobot.sdk.command.data.CommandParamData
import java.util.*


abstract class Command @JvmOverloads constructor(
    /**
     * The name of the command
     */
    val name: String,
    /**
     * The command settings assigned to it
     */
    val settings: CommandSettings = CommandSettings.empty()
) {

    private val data: CommandData

    init {
        data = craftNetwork()
    }

    abstract fun onExecute(sender: CommandSender, alias: String, args: Array<String>): Boolean

    val description: String
        get() = settings.description
    val usageMessage: String
        get() = settings.usageMessage

    fun getData(): CommandData {
        return data
    }

    val aliases: Array<String>
        get() = settings.aliases

    fun craftNetwork(): CommandData {
        val parameterData: Array<Array<CommandParamData>> = arrayOf<Array<CommandParamData>>(
            arrayOf<CommandParamData>(
                CommandParamData(name, true, null, CommandParam.TEXT, null, emptyList())
            )
        )
        val aliases: MutableSet<String> = HashSet(aliases.size + 1)
        Collections.addAll(aliases, *this.aliases)
        aliases.add(name)
        return CommandData(
            name,
            description,
            emptyList(),
            0.toByte(),
            CommandEnumData(name, aliases, false),
            parameterData
        )
    }
}