package cn.hoyobot.sdk.utils

import cn.hoyobot.sdk.HoyoBot
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter
import kotlin.math.roundToInt

object Utils {
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
        requireNotNull(content) { "content must not be null" }
        if (!file.exists()) {
            file.createNewFile()
        }
        val stream = FileOutputStream(file)
        val buffer = ByteArray(1024)
        var length: Int
        while (content.read(buffer).also { length = it } != -1) {
            stream.write(buffer, 0, length)
        }
        stream.close()
        content.close()
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
        val br = BufferedReader(reader)
        var temp: String?
        val stringBuilder = StringBuilder()
        temp = br.readLine()
        while (temp != null) {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.append("\n")
            }
            stringBuilder.append(temp)
            temp = br.readLine()
        }
        br.close()
        reader.close()
        return stringBuilder.toString()
    }

    fun getExceptionMessage(e: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        e.printStackTrace(printWriter)
        return stringWriter.toString()
    }

    fun dataToUUID(vararg params: String?): UUID {
        val builder = StringBuilder()
        for (param in params) {
            builder.append(param)
        }
        return UUID.nameUUIDFromBytes(builder.toString().toByteArray(StandardCharsets.UTF_8))
    }

    fun dataToUUID(vararg params: ByteArray?): UUID {
        val stream = ByteArrayOutputStream()
        for (param in params) {
            try {
                stream.write(param)
            } catch (e: IOException) {
                break
            }
        }
        return UUID.nameUUIDFromBytes(stream.toByteArray())
    }

    fun rtrim(s: String, character: Char): String {
        var i = s.length - 1
        while (i >= 0 && s[i] == character) {
            i--
        }
        return s.substring(0, i + 1)
    }

    fun isByteArrayEmpty(array: ByteArray): Boolean {
        for (b in array) {
            if (b.toInt() != 0) {
                return false
            }
        }
        return true
    }

    fun toRGB(r: Byte, g: Byte, b: Byte, a: Byte): Long {
        var result = (r.toInt() and 0xff).toLong()
        result = result or (g.toInt() and 0xff shl 8).toLong()
        result = result or (b.toInt() and 0xff shl 16).toLong()
        result = result or (a.toInt() and 0xff shl 24).toLong()
        return result and 0xFFFFFFFFL
    }

    fun toInt(number: Any): Int {
        return if (number is Int) {
            number
        } else (number as Double).roundToInt()
    }

    fun parseHexBinary(s: String): ByteArray {
        val len = s.length

        // "111" is not a valid hex encoding.
        require(len % 2 == 0) { "hexBinary needs to be even-length: $s" }
        val out = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            val h = hexToBin(s[i])
            val l = hexToBin(s[i + 1])
            require(!(h == -1 || l == -1)) { "contains illegal character for hexBinary: $s" }
            out[i / 2] = (h * 16 + l).toByte()
            i += 2
        }
        return out
    }

    fun toRSASecret(): String {
        val sighKey = SecretKeySpec(HoyoBot.instance.getBot().botKey.toByteArray(Charsets.UTF_8), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(sighKey)
        val raw = mac.doFinal(HoyoBot.instance.getBot().botSecret.toByteArray(Charsets.UTF_8))
        return DatatypeConverter.printHexBinary(raw).lowercase(Locale.getDefault())
    }

    private fun hexToBin(ch: Char): Int {
        if (ch in '0'..'9') return ch.code - '0'.code
        if (ch in 'A'..'F') return ch.code - 'A'.code + 10
        return if (ch in 'a'..'f') ch.code - 'a'.code + 10 else -1
    }
}