package cn.hoyobot.sdk.command.console

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.command.CommandMap
import org.jline.reader.Candidate
import org.jline.reader.Completer
import org.jline.reader.LineReader
import org.jline.reader.ParsedLine
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList


class CommandCompleter(proxy: HoyoBot) : Completer {
    private val proxy: HoyoBot

    init {
        this.proxy = proxy
    }

    private fun addOptions(commandConsumer: Consumer<String>) {
        val commandMap: CommandMap = proxy.getCommandMap()
        for (command in commandMap.commands.keys) {
            commandConsumer.accept(command)
        }
    }

    override fun complete(lineReader: LineReader, parsedLine: ParsedLine, candidates: MutableList<Candidate>) {
        if (parsedLine.wordIndex() == 0) {
            if (parsedLine.word().isEmpty()) {
                addOptions { command: String ->
                    candidates.add(
                        Candidate(command)
                    )
                }
                return
            }
            val commands: MutableList<String> = ArrayList()
            addOptions { e: String -> commands.add(e) }
            for (command in commands) {
                if (command.startsWith(parsedLine.word())) {
                    candidates.add(Candidate(command))
                }
            }
            return
        }
        if (parsedLine.wordIndex() > 1 && parsedLine.word().isNotEmpty()) {
            val world = parsedLine.word()
            //自动补全米游社UID
            proxy.getBot().getMemberList("", 10).members.forEach {
                run {
                    if (it.uid.toString().lowercase(Locale.getDefault()).startsWith(world)) {
                        candidates.add(Candidate(it.uid.toString()))
                    }
                }
            }
        }
    }
}