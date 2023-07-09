package cn.hoyobot.sdk.command.data

class CommandParam {
    private val paramType: CommandParamType
    val defaultValue: Int

    constructor(paramType: CommandParamType) {
        this.paramType = paramType
        defaultValue = -1
    }

    constructor(defaultValue: Int) {
        this.defaultValue = defaultValue
        paramType = CommandParamType.COMMAND
    }

    override fun toString(): String {
        return "CommandParam(type=" + (paramType.name) + ", defaultValue=" + defaultValue + ")"
    }

    constructor(paramType: CommandParamType, defaultValue: Int) {
        this.paramType = paramType
        this.defaultValue = defaultValue
    }

    fun getParamType(): CommandParamType {
        return paramType
    }

    companion object {
        val INT: CommandParam
        val FLOAT: CommandParam
        val VALUE: CommandParam
        val WILDCARD_INT: CommandParam
        val OPERATOR: CommandParam
        val TARGET: CommandParam
        val WILDCARD_TARGET: CommandParam
        val FILE_PATH: CommandParam
        val INT_RANGE: CommandParam
        val STRING: CommandParam
        val POSITION: CommandParam
        val BLOCK_POSITION: CommandParam
        val MESSAGE: CommandParam
        val TEXT: CommandParam
        val JSON: CommandParam
        val COMMAND: CommandParam

        init {
            INT = CommandParam(CommandParamType.INT)
            FLOAT = CommandParam(CommandParamType.FLOAT)
            VALUE = CommandParam(CommandParamType.VALUE)
            WILDCARD_INT = CommandParam(CommandParamType.WILDCARD_INT)
            OPERATOR = CommandParam(CommandParamType.OPERATOR)
            TARGET = CommandParam(CommandParamType.TARGET)
            WILDCARD_TARGET = CommandParam(CommandParamType.WILDCARD_TARGET)
            FILE_PATH = CommandParam(CommandParamType.FILE_PATH)
            INT_RANGE = CommandParam(CommandParamType.INT_RANGE)
            STRING = CommandParam(CommandParamType.STRING)
            POSITION = CommandParam(CommandParamType.POSITION)
            BLOCK_POSITION = CommandParam(CommandParamType.BLOCK_POSITION)
            MESSAGE = CommandParam(CommandParamType.MESSAGE)
            TEXT = CommandParam(CommandParamType.TEXT)
            JSON = CommandParam(CommandParamType.JSON)
            COMMAND = CommandParam(CommandParamType.COMMAND)
        }
    }
}