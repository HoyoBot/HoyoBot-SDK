package cn.hoyobot.sdk.network.protocol

import cn.hoyobot.sdk.HoyoBot
import cn.hutool.core.convert.Convert
import cn.hutool.core.date.DateUtil
import cn.hutool.core.net.NetUtil
import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.core.util.URLUtil
import cn.hutool.json.JSONObject
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.HttpHeaderValues.*
import io.netty.handler.codec.http.cookie.Cookie
import io.netty.handler.codec.http.cookie.ServerCookieDecoder
import io.netty.handler.codec.http.multipart.*
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.charset.Charset
import java.util.*


open class ProxyRequest private constructor(ctx: ChannelHandlerContext, private val nettyRequest: FullHttpRequest) {
    val path: String
    var jsonData: JSONObject = JSONObject()
    var body: String = ""
    private var ip: String? = null
    private val headers: MutableMap<String, String> = HashMap()
    private val params: MutableMap<String, Any> = HashMap()
    private val cookies: MutableMap<String, Cookie> = HashMap()

    init {
        try {
            this.body = nettyRequest.content().toString(Charsets.UTF_8)
            if (nettyRequest.method() == HttpMethod.POST) this.jsonData =
                JSONObject(nettyRequest.content().toString(Charsets.UTF_8))
            if (this.jsonData.isEmpty()) {
                val content: ByteBuf = nettyRequest.content()
                val reqContent = ByteArray(content.readableBytes())
                this.jsonData = JSONObject(reqContent.toString(Charset.forName("UTF-8")))
            }
        } catch (_: Exception) {
        }
        val uri = nettyRequest.uri()
        path = URLUtil.getPath(this.uri)
        putHeadersAndCookies(nettyRequest.headers())
        this.putParams(QueryStringDecoder(uri))
        if (nettyRequest.method() !== HttpMethod.GET && "application/octet-stream" != nettyRequest.headers()["Content-Type"]) {
            var decoder: HttpPostRequestDecoder? = null
            try {
                decoder = HttpPostRequestDecoder(HTTP_DATA_FACTORY, nettyRequest)
                this.putParams(decoder)
            } finally {
                decoder?.destroy()
            }
        }
        putIp(ctx)
    }

    fun getNettyRequest(): HttpRequest {
        return nettyRequest
    }

    private val protocolVersion: String
        get() = nettyRequest.protocolVersion().text()
    private val uri: String
        get() = nettyRequest.uri()
    private val method: String
        get() = nettyRequest.method().name()

    fun getHeaders(): Map<String, String> {
        return headers
    }

    fun getHeader(headerKey: String): String? {
        return headers[headerKey]
    }

    val isXWwwFormUrlencoded: Boolean
        get() = "application/x-www-form-urlencoded" == getHeader("Content-Type")

    fun getCookie(name: String): Cookie? {
        return cookies[name]
    }

    fun getCookies(): Map<String, Cookie> {
        return cookies
    }

    val isIE: Boolean
        get() {
            var userAgent = getHeader("User-Agent")
            if (StrUtil.isNotBlank(userAgent)) {
                userAgent = userAgent!!.uppercase(Locale.getDefault())
                if (userAgent.contains("MSIE") || userAgent.contains("TRIDENT")) {
                    return true
                }
            }
            return false
        }

    private fun getParam(name: String): String? {
        val value = params[name] ?: return null
        return if (value is String) {
            value
        } else value.toString()
    }

    fun getObjParam(name: String): Any? {
        return params[name]
    }

    fun getParam(name: String, charset: Charset?): String? {
        var charset = charset
        if (null == charset) {
            charset = Charset.forName(CharsetUtil.ISO_8859_1)
        }
        var destCharset = CharsetUtil.UTF_8
        if (isIE) {
            destCharset = CharsetUtil.GBK
        }
        var value = getParam(name)
        if (METHOD_GET.equals(method, ignoreCase = true)) {
            value = CharsetUtil.convert(value, charset.toString(), destCharset)
        }
        return value
    }

    fun getParam(name: String, defaultValue: String): String {
        val param = getParam(name)
        return if (StrUtil.isBlank(param)) defaultValue else param!!
    }

    fun getIntParam(name: String, defaultValue: Int?): Int {
        return Convert.toInt(getParam(name), defaultValue)
    }

    fun getLongParam(name: String, defaultValue: Long?): Long {
        return Convert.toLong(getParam(name), defaultValue)
    }

    fun getDoubleParam(name: String, defaultValue: Double?): Double {
        return Convert.toDouble(getParam(name), defaultValue)
    }

    fun getFloatParam(name: String, defaultValue: Float?): Float {
        return Convert.toFloat(getParam(name), defaultValue)
    }

    fun getBoolParam(name: String, defaultValue: Boolean?): Boolean {
        return Convert.toBool(getParam(name), defaultValue)
    }

    fun getDateParam(name: String, defaultValue: Date): Date {
        val param = getParam(name)
        return if (StrUtil.isBlank(param)) defaultValue else DateUtil.parse(param)
    }

    fun getDateParam(name: String, format: String?, defaultValue: Date): Date {
        val param = getParam(name)
        return if (StrUtil.isBlank(param)) defaultValue else DateUtil.parse(param, format)
    }

    fun getArrayParam(name: String): List<String>? {
        val value = params[name] ?: return null
        return if (value is List<*>) {
            value as List<String>
        } else if (value is String) {
            StrUtil.split(value, ',')
        } else {
            throw RuntimeException("Value is not a List type!")
        }
    }

    fun getParams(): Map<String, Any> {
        return params
    }

    val isKeepAlive: Boolean
        get() {
            val connectionHeader = getHeader(HttpHeaderNames.CONNECTION.toString())
            if (CLOSE.toString().equals(connectionHeader, ignoreCase = true)) {
                return false
            }
            if (HttpVersion.HTTP_1_0.text() == protocolVersion) {
                if (!KEEP_ALIVE.toString().equals(connectionHeader, ignoreCase = true)) {
                    return false
                }
            }
            return true
        }

    private fun putParams(decoder: QueryStringDecoder?) {
        if (null != decoder) {
            var valueList: List<String>
            decoder.parameters().forEach { (key, value) ->
                run {
                    valueList = value
                    this.putParam(key, if (1 == valueList.size) valueList[0] else valueList)
                }
            }
        }
    }

    private fun putParams(decoder: HttpPostRequestDecoder) {
        for (data in decoder.bodyHttpDatas) {
            putParam(data)
        }
    }

    private fun putParam(data: InterfaceHttpData) {
        val dataType = data.httpDataType
        if (dataType == HttpDataType.Attribute) {
            val attribute = data as Attribute
            try {
                this.putParam(attribute.name, attribute.value)
            } catch (e: IOException) {
                HoyoBot.instance.getLogger().error(e)
            }
        } else if (dataType == HttpDataType.FileUpload) {
            val fileUpload = data as FileUpload
            if (fileUpload.isCompleted) {
                try {
                    this.putParam(data.getName(), fileUpload.file)
                } catch (e: IOException) {
                    HoyoBot.instance.getLogger().error("Get file param [${data.name}] error!", e)
                }
            }
        }
    }

    fun putParam(key: String, value: Any) {
        params[key] = value
    }

    private fun putHeadersAndCookies(headers: HttpHeaders) {
        headers.forEach { (key, value) ->
            run {
                this.headers[key] = value
            }
        }
        val cookieString = this.headers[HttpHeaderNames.COOKIE.toString()]
        if (StrUtil.isNotBlank(cookieString)) {
            val cookies = ServerCookieDecoder.LAX.decode(cookieString)
            for (cookie in cookies) {
                this.cookies[cookie.name()] = cookie
            }
        }
    }

    private fun putIp(ctx: ChannelHandlerContext) {
        var ip = getHeader("X-Forwarded-For")
        ip = if (StrUtil.isNotBlank(ip)) {
            NetUtil.getMultistageReverseProxyIp(ip)
        } else {
            val insocket = ctx.channel().remoteAddress() as InetSocketAddress
            insocket.address.hostAddress
        }
        this.ip = ip
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("\r\nprotocolVersion: ").append(protocolVersion).append("\r\n")
        sb.append("uri: ").append(uri).append("\r\n")
        sb.append("path: ").append(path).append("\r\n")
        sb.append("method: ").append(method).append("\r\n")
        sb.append("ip: ").append(ip).append("\r\n")
        sb.append("headers:\r\n ")
        headers.forEach { (key, value) -> sb.append("    ").append(key).append(": ").append(value).append("\r\n") }
        sb.append("params: \r\n")
        params.forEach { (key, value) -> sb.append("    ").append(key).append(": ").append(value).append("\r\n") }
        sb.append("jsonData: \r\n ")
        sb.append(this.jsonData.toJSONString(4))
        return sb.toString()
    }

    companion object {
        val METHOD_DELETE: String = HttpMethod.DELETE.name()
        val METHOD_HEAD: String = HttpMethod.HEAD.name()
        val METHOD_GET: String = HttpMethod.GET.name()
        val METHOD_OPTIONS: String = HttpMethod.OPTIONS.name()
        val METHOD_POST: String = HttpMethod.POST.name()
        val METHOD_PUT: String = HttpMethod.PUT.name()
        val METHOD_TRACE: String = HttpMethod.TRACE.name()
        private val HTTP_DATA_FACTORY: HttpDataFactory = DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE)
        fun build(ctx: ChannelHandlerContext, nettyRequest: FullHttpRequest): ProxyRequest {
            return ProxyRequest(ctx, nettyRequest)
        }
    }
}