package cn.hoyobot.sdk.command

class CommandSettings private constructor(
    val usageMessage: String,
    val description: String,
    val aliases: Array<String>,
    val isQuoteAware: Boolean = false
) {

    class Builder {

        var usageMessage = ""
            private set
        var description: String? = null
            private set
        var aliases = emptyArray<String>()
            private set
        var isQuoteAware = false
            private set

        fun build(): CommandSettings {
            return CommandSettings(
                usageMessage,
                (if (description == null) usageMessage else description)!!,
                aliases,
                isQuoteAware
            )
        }
    }

    companion object {
        private val EMPTY_SETTINGS = builder().build()
        fun empty(): CommandSettings {
            return EMPTY_SETTINGS
        }

        fun builder(): Builder {
            return Builder()
        }
    }
}