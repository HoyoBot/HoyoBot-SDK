package cn.hoyobot.sdk.network.protocol

import java.lang.annotation.Inherited

@Retention(AnnotationRetention.RUNTIME)
@Inherited
annotation class ProxyRoute(val value: String)