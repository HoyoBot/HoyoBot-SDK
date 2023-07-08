package cn.hoyobot.sdk


class Launcher {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            HoyoBot().initBotProxy()
        }
    }

}