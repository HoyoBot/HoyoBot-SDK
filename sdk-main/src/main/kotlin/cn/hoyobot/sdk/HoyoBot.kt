package cn.hoyobot.sdk

import cn.hoyobot.sdk.scheduler.BotScheduler
import cn.hoyobot.sdk.utils.Config
import cn.hoyobot.sdk.utils.ConfigSection
import cn.hutool.log.Log
import cn.hutool.log.LogFactory
import lombok.Getter
import java.io.File


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
    private lateinit var properties: Config

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
        properties = Config(this.path + "bot.properties", Config.PROPERTIES, object : ConfigSection() {
            init {
                put("bot_id", "")
                put("bot_secret", "")
                put("server-ip", "0:0:0:0")
                put("port", 88)
                put("http_call_back", "/")
            }
        })


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