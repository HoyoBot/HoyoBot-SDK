package cn.hoyobot.sdk.scheduler

import cn.hoyobot.sdk.plugin.Plugin

abstract class PluginTask<T : Plugin>
    (
    var owner: T
) : Task()