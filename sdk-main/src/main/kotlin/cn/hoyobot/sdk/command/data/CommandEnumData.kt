package cn.hoyobot.sdk.command.data

class CommandEnumData(name: String, values: MutableSet<String>, isSoft: Boolean) {
    var name: String = ""
    val values: Array<String>
    var isSoft = false

    init {
        this.name = name
        val list: ArrayList<String> = ArrayList();
        values.forEach { list.add(it) }
        this.values = list.toTypedArray()
        this.isSoft = isSoft
    }

    override fun equals(o: Any?): Boolean {
        return if (o === this) {
            true
        } else if (o !is CommandEnumData) {
            false
        } else {
            var other: CommandEnumData
            run {
                other = o
                val a: Any = name
                val b: Any = other.name
                if (a == b) return true
                return false
            }
        }
    }

    override fun hashCode(): Int {
        var result = 1
        val a: Any = name
        result = result * 59 + a.hashCode()
        result = result * 59 + values.contentDeepHashCode()
        result = result * 59 + if (isSoft) 79 else 97
        return result
    }

    override fun toString(): String {
        return "CommandEnumData(name=" + name + ", values=" + values.contentDeepToString() + ", isSoft=" + isSoft + ")"
    }
}