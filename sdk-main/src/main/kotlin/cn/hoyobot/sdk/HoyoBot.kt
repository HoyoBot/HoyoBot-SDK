package cn.hoyobot.sdk

import cn.hoyobot.sdk.scheduler.BotScheduler
import cn.hutool.log.Log
import cn.hutool.log.LogFactory
import lombok.Getter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.File

@SpringBootApplication
@Getter
class HoyoBot {

    companion object {
        lateinit var instance: HoyoBot;
    }

    private val version = "1.0.0"
    private val path = System.getProperty("user.dir") + "/"
    private val pluginPath = path + "plugins"
    private val logger: Log = LogFactory.get("HoyoBot")
    private var isRunning = false
    private var botScheduler = BotScheduler()

    fun main(args: Array<String>) {
        instance = this
        this.logger.info("HoyoBot - ${this.version}")
        this.logger.info("Find updates at: https://github.com/HoyoBot/HoyoBot-SDK")
        this.isRunning = true

        //TODO: Plugin Loader
        if (!File(pluginPath).exists()) {
            File(pluginPath).mkdirs()
        }

        this.logger.info("Loading HoyoBot properties...")

        SpringApplication.run(this.javaClass)
    }

    fun getLogger(): Log {
        return this.logger
    }

    fun getPluginPath(): String {
        return this.pluginPath
    }

    fun getScheduler(): BotScheduler {
        return this.botScheduler
    }

}