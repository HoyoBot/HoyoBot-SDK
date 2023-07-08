package cn.hoyobot.sdk

import cn.hutool.log.Log
import cn.hutool.log.LogFactory
import lombok.Getter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@Getter
class HoyoBot {

    companion object {
        lateinit var instance: HoyoBot;
    }

    private val version = "1.0.0"
    private val path = System.getProperty("user.dir") + "/"
    private val pluginPath = path + "plugins"
    private val logger: Log = LogFactory.get("HoyoBot");

    fun main(args: Array<String>) {
        instance = this;
        this.logger.info("HoyoBot - ${this.version}")
        this.logger.info("Find updates at: https://github.com/HoyoBot/HoyoBot-SDK")
        //TODO: Plugin Loader
        SpringApplication.run(this.javaClass)
    }

}