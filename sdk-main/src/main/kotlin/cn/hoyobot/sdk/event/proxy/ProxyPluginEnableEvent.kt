package cn.hoyobot.sdk.event.proxy

import cn.hoyobot.sdk.event.types.ProxyEvent
import cn.hoyobot.sdk.plugin.Plugin

class ProxyPluginEnableEvent(val plugin: Plugin) : ProxyEvent() {
}