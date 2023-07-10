package cn.hoyobot.sdk.command

class CommandSettings private constructor(
    val usageMessage: String,
    val description: String,
    val aliases: Array<String>,
    val isQuoteAware: Boolean = true
) {

    class Builder {

        var usageMessage = ""
            private set
        var description: String = ""
            private set
        var aliases = emptyArray<String>()
            private set
        var isQuoteAware = true
            private set

        fun put(usageMessage: String, description: String, aliases: Array<String>): Builder {
            this.usageMessage = usageMessage
            this.description = description
            this.aliases = aliases
            return this
        }

        fun build(): CommandSettings {
            return CommandSettings(
                usageMessage,
                description,
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