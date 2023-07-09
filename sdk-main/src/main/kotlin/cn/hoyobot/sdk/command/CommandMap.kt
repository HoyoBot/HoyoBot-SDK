package cn.hoyobot.sdk.command


interface CommandMap {
    /**
     * Registers a command with the respective name in it
     *
     * @param command the command which should be registered
     * @return true if this command could be registered under the respective name
     */
    fun registerCommand(command: Command): Boolean

    /**
     * @param name the command name
     * @param command the command which should be registered under the respective name
     * @return true if this command could be registered under the respective name
     */
    @Deprecated(
        """this should be replaced with CommandMap.registerAlias
     
      """
    )
    fun registerCommand(name: String, command: Command): Boolean
    fun registerAlias(name: String, command: Command): Boolean
    fun unregisterCommand(name: String): Boolean

    /**
     * @param name the name of the command or alias, case-insensitive
     * @return The Command instance of the registered Command, or null if no command or alias with this name could be found.
     */
    fun getCommand(name: String): Command
    fun isRegistered(name: String): Boolean

    /**
     * @return if command can be handled by this command map
     */
    fun handleMessage(sender: CommandSender, message: String): Boolean

    /**
     * WARNING: Will return true even if command was handled but thrown exception!
     *
     * @return true if command was handled.
     */
    fun handleCommand(sender: CommandSender, command: String, args: Array<String>): Boolean
    val commandPrefix: String
    val commands: HashMap<String, Command>
}