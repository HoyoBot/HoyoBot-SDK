package cn.hoyobot.sdk.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*
import java.nio.charset.StandardCharsets


object FileUtils {
    const val INT_MEGABYTE = 1048576
    private var GSON: Gson? = null

    init {
        val builder = GsonBuilder()
        GSON = builder.create()
    }

    @Throws(IOException::class)
    fun saveFromResources(fileName: String?, targetFile: File) {
        if (targetFile.exists()) {
            return
        }
        targetFile.createNewFile()
        val inputStream = FileUtils::class.java.classLoader.getResourceAsStream(fileName)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        val outputStream: OutputStream = FileOutputStream(targetFile)
        outputStream.write(buffer)
        inputStream.close()
    }

    @Throws(IOException::class)
    fun readFile(file: File): String {
        if (!file.exists() || file.isDirectory) {
            throw FileNotFoundException()
        }
        return readFile(FileInputStream(file))
    }

    @Throws(IOException::class)
    fun readFile(filename: String?): String {
        val file = File(filename)
        if (!file.exists() || file.isDirectory) {
            throw FileNotFoundException()
        }
        return readFile(FileInputStream(file))
    }

    @Throws(IOException::class)
    fun readFile(inputStream: InputStream?): String {
        return readFile(InputStreamReader(inputStream, StandardCharsets.UTF_8))
    }

    @Throws(IOException::class)
    private fun readFile(reader: Reader): String {
        BufferedReader(reader).use { br ->
            var temp: String?
            val stringBuilder = StringBuilder()
            temp = br.readLine()
            while (temp != null) {
                if (stringBuilder.length != 0) {
                    stringBuilder.append("\n")
                }
                stringBuilder.append(temp)
                temp = br.readLine()
            }
            return stringBuilder.toString()
        }
    }

    @Throws(IOException::class)
    fun writeFile(fileName: String?, content: String) {
        writeFile(fileName, ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8)))
    }

    @Throws(IOException::class)
    fun writeFile(fileName: String?, content: InputStream?) {
        writeFile(File(fileName), content)
    }

    @Throws(IOException::class)
    fun writeFile(file: File, content: String) {
        writeFile(file, ByteArrayInputStream(content.toByteArray(StandardCharsets.UTF_8)))
    }

    @Throws(IOException::class)
    fun writeFile(file: File, content: InputStream?) {
        requireNotNull(content) { "Content must not be null!" }
        if (!file.exists()) {
            file.createNewFile()
        }
        val stream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var length: Int
        while (content.read(buffer).also { length = it } != -1) {
            stream.write(buffer, 0, length)
        }
        content.close()
        stream.close()
    }
}