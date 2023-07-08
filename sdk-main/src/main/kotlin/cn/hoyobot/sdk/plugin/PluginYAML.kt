package cn.hoyobot.sdk.plugin

import lombok.ToString


@ToString
class PluginYAML {
    var name: String = ""
    var version: String = ""
    var author: String = ""
    var main: String = ""
    var depends: List<String> = arrayListOf("")
}