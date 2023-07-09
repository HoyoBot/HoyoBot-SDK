package cn.hoyobot.sdk.command

import cn.hoyobot.sdk.HoyoBot

class DefaultCommandMap(proxy: HoyoBot, prefix: String) : SimpleCommandMap(proxy, prefix) {
    init {
        registerDefaults()
    }

    fun registerDefaults() {}
}