package cn.hoyobot.sdk.command.console

class ConsoleThread(console: TerminalConsole) : Thread("HoyoBot Console") {
    private val console: TerminalConsole

    init {
        this.console = console
    }

    override fun run() {
        console.start()
    }
}