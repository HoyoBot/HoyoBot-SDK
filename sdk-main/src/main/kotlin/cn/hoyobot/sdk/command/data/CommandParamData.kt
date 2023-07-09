package cn.hoyobot.sdk.command.data


class CommandParamData(
    name: String,
    optional: Boolean,
    enumData: CommandEnumData?,
    type: CommandParam,
    postfix: String?,
    options: List<CommandParamOption>
) {
    var name: String = ""
    var isOptional = false
    lateinit var enumData: CommandEnumData
    var type: CommandParam
    var postfix: String = ""
    private val options: List<CommandParamOption>

    init {
        this.name = name
        isOptional = optional
        if (enumData != null) {
            this.enumData = enumData
        }
        this.type = type
        if (postfix != null) {
            this.postfix = postfix
        }
        this.options = options
    }

    fun getOptions(): List<CommandParamOption> {
        return options
    }

    override fun equals(o: Any?): Boolean {
        return if (o === this) {
            true
        } else if (o !is CommandParamData) {
            false
        } else {
            var other: CommandParamData
            run {
                other = o
                val a: Any = name
                val b: Any = other.name
                if (a == b) {
                    return true
                }
                return false
            }
        }
    }

    override fun hashCode(): Int {
        var result = 1
        val name2: Any = name
        result = result * 59 + name2.hashCode()
        result = result * 59 + if (isOptional) 79 else 97
        val enumData2: Any = enumData
        result = result * 59 + enumData2.hashCode()
        val type2: Any = type
        result = result * 59 + type2.hashCode()
        val postfix2: Any = postfix
        result = result * 59 + postfix2.hashCode()
        val options2: Any = getOptions()
        result = result * 59 + options2.hashCode()
        return result
    }

    override fun toString(): String {
        return "CommandParamData(name=" + name + ", optional=" + isOptional + ", enumData=" + enumData + ", type=" + type + ", postfix=" + postfix + ", options=" + getOptions() + ")"
    }

    class Builder(val name: String, type: CommandSymbolData, optional: Boolean, options: Byte) {
        private val type: CommandSymbolData
        val isOptional: Boolean
        val options: Byte

        @Deprecated("")
        constructor(name: String, type: CommandSymbolData, optional: Boolean) : this(name, type, optional, 0.toByte()) {
        }

        fun getType(): CommandSymbolData {
            return type
        }

        override fun equals(o: Any?): Boolean {
            return if (o === this) {
                true
            } else if (o !is Builder) {
                false
            } else {
                var other: Builder
                run {
                    other = o
                    val a: Any = name
                    val b: Any = other.name
                    if (a == b) {
                        return true
                    }
                    return false
                }
            }
        }

        override fun hashCode(): Int {
            var result = 1
            val name2: Any = name
            result = result * 59 + name2.hashCode()
            val type2: Any = getType()
            result = result * 59 + type2.hashCode()
            result = result * 59 + if (isOptional) 79 else 97
            result = result * 59 + options
            return result
        }

        override fun toString(): String {
            return "CommandParamData.Builder(name=" + name + ", type=" + getType() + ", optional=" + isOptional + ", options=" + options + ")"
        }

        init {
            this.type = type
            isOptional = optional
            this.options = options
        }
    }
}