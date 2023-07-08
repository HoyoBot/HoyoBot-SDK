package cn.hoyobot.sdk.scheduler

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.utils.Utils
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets

class FileWriteTask : AsyncTask {
    private var file: File
    private var contents: InputStream

    constructor(path: String?, contents: String) : this(File(path), contents) {}
    constructor(path: String?, contents: ByteArray?) : this(File(path), contents) {}
    constructor(path: String?, contents: InputStream) {
        file = File(path)
        this.contents = contents
    }

    constructor(file: File, contents: String) {
        this.file = file
        this.contents = ByteArrayInputStream(contents.toByteArray(StandardCharsets.UTF_8))
    }

    constructor(file: File, contents: ByteArray?) {
        this.file = file
        this.contents = ByteArrayInputStream(contents)
    }

    constructor(file: File, contents: InputStream) {
        this.file = file
        this.contents = contents
    }

    override fun onRun() {
        try {
            Utils.writeFile(file, contents)
        } catch (e: IOException) {
            HoyoBot.instance.getLogger().error(e)
        }
    }
}