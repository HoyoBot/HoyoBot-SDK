package cn.hoyobot.sdk.event.proxy

import cn.hoyobot.sdk.event.CancellableEvent
import cn.hoyobot.sdk.event.types.ProxyEvent
import cn.hoyobot.sdk.network.BotEntry
import cn.hoyobot.sdk.network.protocol.mihoyo.Message

class ProxySendMessageEvent(val sender: BotEntry, val message: Message) : ProxyEvent(), CancellableEvent {

    var roomID = 0
    var villaID = ""

}