package cn.hoyobot.sdk.command.data

import java.util.*

class CommandData(
    val name: String,
    val description: String,
    val flags: List<Flag>,
    val permission: Byte,
    aliases: CommandEnumData,
    overloads: Array<Array<CommandParamData>>
) {
    private val aliases: CommandEnumData
    private val overloads: Array<Array<CommandParamData>>
    override fun toString(): String {
        val overloads = StringBuilder("[\r\n")
        val var2: Array<Array<CommandParamData>> = this.overloads
        val var3 = var2.size
        for (var4 in 0 until var3) {
            val overload: Array<CommandParamData> = var2[var4]
            overloads.append("    [\r\n")
            val var6: Array<CommandParamData> = overload
            val var7 = overload.size
            for (var8 in 0 until var7) {
                val parameter: CommandParamData = var6[var8]
                overloads.append("       ").append(parameter).append("\r\n")
            }
            overloads.append("    ]\r\n")
        }
        overloads.append("]\r\n")
        val builder = StringBuilder("CommandData(\r\n")
        val objects: List<*> = listOf(
            "name=$name",
            "description=$description",
            "flags=" + flags.toTypedArray().contentToString(),
            "permission=$permission",
            "aliases=$aliases",
            "overloads=$overloads"
        )
        val var12 = objects.iterator()
        while (var12.hasNext()) {
            val `object` = var12.next()!!
            builder.append("    ").append(Objects.toString(`object`).replace("\r\n".toRegex(), "\r\n    "))
                .append("\r\n")
        }
        return builder.append(")").toString()
    }

    init {
        this.aliases = aliases
        this.overloads = overloads
    }

    fun getAliases(): CommandEnumData {
        return aliases
    }

    fun getOverloads(): Array<Array<CommandParamData>> {
        return overloads
    }

    override fun equals(o: Any?): Boolean {
        return if (o === this) {
            true
        } else if (o !is CommandData) {
            false
        } else {
            val a2: Any = name
            val b2: Any = o.name
            if (a2 != b2) {
                return false
            }
            run {
                val a: Any = description
                val b: Any = o.description
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
        val b2: Any = description
        result = result * 59 + b2.hashCode()
        val c2: Any = flags
        result = result * 59 + c2.hashCode()
        result = result * 59 + permission
        val d2: Any = getAliases()
        result = result * 59 + d2.hashCode()
        result = result * 59 + getOverloads().contentDeepHashCode()
        return result
    }

    enum class Flag {
        USAGE, VISIBILITY, SYNC, EXECUTE, TYPE, CHEAT, UNKNOWN_6
    }

    class Builder(
        val name: String,
        val description: String,
        val flags: Int,
        val permission: Int,
        val aliases: Int,
        overloads: Array<Array<CommandParamData.Builder>>
    ) {
        private val overloads: Array<Array<CommandParamData.Builder>>

        init {
            this.overloads = overloads
        }

        fun getOverloads(): Array<Array<CommandParamData.Builder>> {
            return overloads
        }

        override fun equals(o: Any?): Boolean {
            return if (o === this) {
                true
            } else if (o !is Builder) {
                false
            } else {
                val a: Any = name
                val b: Any = o.name
                if (a != b) {
                    return false
                }
                run {
                    val des2: Any = description
                    val des3: Any = o.description
                    if (des2 == des3) {
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
            val des2: Any = description
            result = result * 59 + des2.hashCode()
            result = result * 59 + flags
            result = result * 59 + permission
            result = result * 59 + aliases
            result = result * 59 + getOverloads().contentDeepHashCode()
            return result
        }

        override fun toString(): String {
            return "CommandData.Builder(name=$name, description=$description, flags=$flags, permission=$permission, aliases=$aliases, overloads=" + getOverloads().contentDeepToString() + ")"
        }
    }
}