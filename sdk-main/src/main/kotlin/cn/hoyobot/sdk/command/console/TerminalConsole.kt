package cn.hoyobot.sdk.command.console

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.CommandSender
import net.minecrell.terminalconsole.SimpleTerminalConsole
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder


class TerminalConsole(proxy: HoyoBot) : SimpleTerminalConsole() {
    private val proxy: HoyoBot
    val consoleThread: ConsoleThread

    init {
        this.proxy = proxy
        consoleThread = ConsoleThread(this)
    }

    override fun runCommand(command: String) {
        val console: CommandSender = proxy.getConsoleSender()
        proxy.getScheduler().scheduleTask({ proxy.dispatchCommand(console, command) }, false)
    }

    override fun buildReader(builder: LineReaderBuilder): LineReader? {
        builder.completer(CommandCompleter(proxy))
        builder.appName("HoyoBot Console")
        builder.option(LineReader.Option.HISTORY_BEEP, false)
        builder.option(LineReader.Option.HISTORY_IGNORE_DUPS, true)
        builder.option(LineReader.Option.HISTORY_IGNORE_SPACE, true)
        return super.buildReader(builder)
    }

    override fun shutdown() {
        proxy.shutdown()
    }

    override fun isRunning(): Boolean {
        return proxy.isRunning()
    }
}