package cn.hoyobot.sdk.event.proxy

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.event.types.ProxyEvent

class ProxyBotStopEvent(bot: HoyoBot) : ProxyEvent() {

    val botEntry = bot.getBot()

}