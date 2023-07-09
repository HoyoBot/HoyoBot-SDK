package cn.hoyobot.sdk.logger

import cn.hoyobot.sdk.HoyoBot
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

open class Logger {

    private lateinit var logFile: File
    private var fw: FileWriter? = null
    private var writer: BufferedWriter? = null
    private var deleteCount = 0

    init {
        try {
            File(
                HoyoBot.instance.getPath() + File.separator + "logs", "HoyoBot_log_" + fileTime() + ".txt"
            ).also { this.logFile = it }
            val path = File(HoyoBot.instance.getPath() + File.separator + "logs")
            if (!path.exists()) {
                path.mkdirs()
            }

            this.recursiveDeleteFilesOlderThanNDays(
                HoyoBot.instance.getDiscardOldLogsDays(),
                HoyoBot.instance.getPath() + File.separator + "logs"
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun info(message: String) {
        try {
            HoyoBot.instance.getSubLogger().info(message)
            fw = FileWriter(logFile, true)
            writer = fw?.let { BufferedWriter(it) }
            writer!!.write("[INFO] $time $message")
            writer!!.newLine()
            writer!!.close()
            fw!!.close()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }

    fun error(message: String, exception: Throwable) {
        this.error("$message\n${exception.stackTraceToString()}")
    }

    fun error(exception: Throwable) {
        this.error(exception.stackTraceToString())
    }

    fun debug(message: String) {
        try {
            HoyoBot.instance.getSubLogger().info(message)
            fw = FileWriter(logFile, true)
            writer = fw?.let { BufferedWriter(it) }
            writer!!.write("[DEBUG] $time $message")
            writer!!.newLine()
            writer!!.close()
            fw!!.close()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }

    fun warn(message: String) {
        try {
            HoyoBot.instance.getSubLogger().info(message)
            fw = FileWriter(logFile, true)
            writer = fw?.let { BufferedWriter(it) }
            writer!!.write("[WARNING] $time $message")
            writer!!.newLine()
            writer!!.close()
            fw!!.close()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }

    fun error(message: String) {
        try {
            HoyoBot.instance.getSubLogger().info(message)
            fw = FileWriter(logFile, true)
            writer = fw?.let { BufferedWriter(it) }
            writer!!.write("[ERROR] $time $message")
            writer!!.newLine()
            writer!!.close()
            fw!!.close()
        } catch (var2: Exception) {
            var2.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun recursiveDeleteFilesOlderThanNDays(days: Int, dirPath: String?) {
        val cutOff = System.currentTimeMillis() - days.toLong() * 24 * 60 * 60 * 1000
        Files.list(dirPath?.let { Paths.get(it) })
            .forEach { path: Path ->
                if (Files.isDirectory(path)) {
                    try {
                        recursiveDeleteFilesOlderThanNDays(days, path.toString())
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    try {
                        if (Files.getLastModifiedTime(path)
                                .to(TimeUnit.MILLISECONDS) < cutOff
                        ) {
                            Files.delete(path)
                            this.deleteCount++
                        }
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            }
    }

    private val time: String
        get() {
            val d = Date()
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            return "[" + sdf.format(d) + "]"
        }

    private fun fileTime(): String {
        val d = Date()
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
        return sdf.format(d)
    }

}