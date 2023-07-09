package cn.hoyobot.sdk.command.data

import com.google.common.base.Preconditions


class CommandSymbolData(val value: Int, val isCommandEnum: Boolean, val isSoftEnum: Boolean, val isPostfix: Boolean) {

    fun serialize(): Int {
        var value = value
        if (isCommandEnum) {
            value = value or 2097152
        }
        if (isSoftEnum) {
            value = value or 67108864
        }
        value = if (isPostfix) {
            value or 16777216
        } else {
            value or 1048576
        }
        return value
    }

    override fun equals(o: Any?): Boolean {
        return if (o === this) {
            true
        } else if (o !is CommandSymbolData) {
            false
        } else {
            if (value != o.value) {
                false
            } else if (isCommandEnum != o.isCommandEnum) {
                false
            } else if (isSoftEnum != o.isSoftEnum) {
                false
            } else {
                isPostfix == o.isPostfix
            }
        }
    }

    override fun hashCode(): Int {
        var result = 1
        result = result * 59 + value
        result = result * 59 + if (isCommandEnum) 79 else 97
        result = result * 59 + if (isSoftEnum) 79 else 97
        result = result * 59 + if (isPostfix) 79 else 97
        return result
    }

    override fun toString(): String {
        return "CommandSymbolData(value=$value, commandEnum=$isCommandEnum, softEnum=$isSoftEnum, postfix=$isPostfix)"
    }

    companion object {
        private const val ARG_FLAG_VALID = 1048576
        private const val ARG_FLAG_ENUM = 2097152
        private const val ARG_FLAG_POSTFIX = 16777216
        private const val ARG_FLAG_SOFT_ENUM = 67108864
        fun deserialize(type: Int): CommandSymbolData {
            val value = type and '\uffff'.code
            val commandEnum = type and 2097152 != 0
            val softEnum = type and 67108864 != 0
            val postfix = type and 16777216 != 0
            Preconditions.checkState(
                postfix || type and 1048576 != 0,
                "Invalid command param type: $type"
            )
            return CommandSymbolData(value, commandEnum, softEnum, postfix)
        }
    }
}