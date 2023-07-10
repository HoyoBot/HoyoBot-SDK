package cn.hoyobot.sdk.event.types

import cn.hoyobot.sdk.event.BotEvent
import cn.hoyobot.sdk.event.CancellableEvent
import cn.hoyobot.sdk.network.protocol.type.ProtocolEventType

abstract class VillaEvent(val type: ProtocolEventType) : BotEvent(), CancellableEvent