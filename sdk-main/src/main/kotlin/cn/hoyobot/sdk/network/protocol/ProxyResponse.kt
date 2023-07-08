package cn.hoyobot.sdk.network.protocol

import cn.hoyobot.sdk.HoyoBot
import cn.hoyobot.sdk.network.RaknetInfo
import cn.hoyobot.sdk.network.dispatcher.ProxyFileListener
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpUtil
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.DefaultFileRegion
import io.netty.handler.codec.http.*
import io.netty.handler.codec.http.cookie.Cookie
import io.netty.handler.codec.http.cookie.DefaultCookie
import io.netty.handler.codec.http.cookie.ServerCookieEncoder
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


class ProxyResponse(private val ctx: ChannelHandlerContext, private val request: ProxyRequest) {
    private var httpVersion = HttpVersion.HTTP_1_1
    private var status = HttpResponseStatus.OK
    private var contentType = CONTENT_TYPE_HTML
    private var charset: String = RaknetInfo.charset
    private val headers: HttpHeaders = DefaultHttpHeaders()
    private val cookies: MutableSet<Cookie> = HashSet()
    private var content: Any = Unpooled.EMPTY_BUFFER

    var isSent = false

    fun setHttpVersion(httpVersion: HttpVersion): ProxyResponse {
        this.httpVersion = httpVersion
        return this
    }

    fun setStatus(status: HttpResponseStatus): ProxyResponse {
        this.status = status
        return this
    }

    fun setStatus(status: Int): ProxyResponse {
        return setStatus(HttpResponseStatus.valueOf(status))
    }

    fun setContentType(contentType: String): ProxyResponse {
        this.contentType = contentType
        return this
    }

    fun setCharset(charset: String): ProxyResponse {
        this.charset = charset
        return this
    }

    fun addHeader(name: String, value: Any): ProxyResponse {
        headers.add(name, value)
        return this
    }

    fun setHeader(name: String, value: Any): ProxyResponse {
        headers[name] = value
        return this
    }

    fun setContentLength(contentLength: Long): ProxyResponse {
        setHeader(HttpHeaderNames.CONTENT_LENGTH.toString(), contentLength)
        return this
    }

    fun setKeepAlive(): ProxyResponse {
        setHeader(HttpHeaderNames.CONNECTION.toString(), HttpHeaderValues.KEEP_ALIVE.toString())
        return this
    }

    fun addCookie(cookie: Cookie): ProxyResponse {
        cookies.add(cookie)
        return this
    }

    fun addCookie(name: String, value: String): ProxyResponse {
        return addCookie(DefaultCookie(name, value))
    }

    @JvmOverloads
    fun addCookie(
        name: String,
        value: String,
        maxAgeInSeconds: Int,
        path: String = "/",
        domain: String
    ): ProxyResponse {
        val cookie: Cookie = DefaultCookie(name, value)
        cookie.setDomain(domain)
        cookie.setMaxAge(maxAgeInSeconds.toLong())
        cookie.setPath(path)
        return addCookie(cookie)
    }

    fun setContent(contentText: String): ProxyResponse {
        content = Unpooled.copiedBuffer(contentText, Charset.forName(charset))
        return this
    }

    fun setTextContent(contentText: String): ProxyResponse {
        setContentType(CONTENT_TYPE_TEXT)
        return setContent(contentText)
    }

    fun setJsonContent(contentText: String): ProxyResponse {
        setContentType(if (request.isIE) CONTENT_TYPE_JSON else CONTENT_TYPE_JSON)
        return setContent(contentText)
    }

    fun setXmlContent(contentText: String): ProxyResponse {
        setContentType(CONTENT_TYPE_XML)
        return setContent(contentText)
    }

    fun setContent(contentBytes: ByteArray): ProxyResponse {
        return setContent(Unpooled.copiedBuffer(contentBytes))
    }

    fun setContent(byteBuf: ByteBuf): ProxyResponse {
        content = byteBuf
        return this
    }

    fun setContent(file: File): ProxyResponse {
        content = file
        return this
    }

    fun setDateAndCache(lastModify: Long, httpCacheSeconds: Int) {
        val formatter = SimpleDateFormat(DatePattern.HTTP_DATETIME_PATTERN, Locale.US)
        formatter.timeZone = TimeZone.getTimeZone("GMT")
        val time: Calendar = GregorianCalendar()
        setHeader(HttpHeaderNames.DATE.toString(), formatter.format(time.time))
        time.add(Calendar.SECOND, httpCacheSeconds)
        setHeader(HttpHeaderNames.EXPIRES.toString(), formatter.format(time.time))
        setHeader(HttpHeaderNames.CACHE_CONTROL.toString(), "private, max-age=$httpCacheSeconds")
        setHeader(HttpHeaderNames.LAST_MODIFIED.toString(), formatter.format(DateUtil.date(lastModify)))
    }

    private fun toDefaultHttpResponse(): DefaultHttpResponse {
        val defaultHttpResponse = DefaultHttpResponse(httpVersion, status)
        fillHeadersAndCookies(defaultHttpResponse.headers())
        return defaultHttpResponse
    }

    private fun toFullHttpResponse(): FullHttpResponse {
        val byteBuf = content as ByteBuf
        val fullHttpResponse: FullHttpResponse = DefaultFullHttpResponse(httpVersion, status, byteBuf)
        val httpHeaders = fullHttpResponse.headers()
        fillHeadersAndCookies(httpHeaders)
        httpHeaders[HttpHeaderNames.CONTENT_LENGTH.toString()] = byteBuf.readableBytes()
        return fullHttpResponse
    }

    private fun fillHeadersAndCookies(httpHeaders: HttpHeaders) {
        httpHeaders[HttpHeaderNames.CONTENT_TYPE.toString()] = StrUtil.format("{};charset={}", contentType, charset)
        httpHeaders[HttpHeaderNames.CONTENT_ENCODING.toString()] = charset
        for (cookie in cookies) {
            httpHeaders.add(HttpHeaderNames.SET_COOKIE.toString(), ServerCookieEncoder.LAX.encode(cookie))
        }
    }

    fun send(): ChannelFuture? {
        val channelFuture: ChannelFuture? = if (content is File) {
            val file = content as File
            try {
                sendFile(file)
            } catch (e: IOException) {
                HoyoBot.instance.getLogger().error(StrUtil.format("Send {} error!", file), e)
                sendError(HttpResponseStatus.FORBIDDEN, "")
            }
        } else {
            sendFull()
        }
        isSent = true
        return channelFuture
    }

    private fun sendFull(): ChannelFuture {
        return if (request.isKeepAlive) {
            setKeepAlive()
            ctx.writeAndFlush(toFullHttpResponse())
        } else {
            sendAndCloseFull()
        }
    }

    private fun sendAndCloseFull(): ChannelFuture {
        return ctx.writeAndFlush(toFullHttpResponse()).addListener(ChannelFutureListener.CLOSE)
    }

    @Throws(IOException::class)
    private fun sendFile(file: File): ChannelFuture {
        val raf = RandomAccessFile(file, "r")
        val fileLength = raf.length()
        setContentLength(fileLength)
        var contentType = HttpUtil.getMimeType(file.name)
        if (StrUtil.isBlank(contentType)) {
            contentType = "application/octet-stream"
        }
        setContentType(contentType)
        ctx.write(toDefaultHttpResponse())
        ctx.write(DefaultFileRegion(raf.channel, 0, fileLength), ctx.newProgressivePromise())
            .addListener(ProxyFileListener.build(raf))
        return sendEmptyLast()
    }

    private fun sendEmptyLast(): ChannelFuture {
        val lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
        if (!request.isKeepAlive) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE)
        }
        return lastContentFuture
    }

    fun sendRedirect(uri: String): ChannelFuture? {
        return this.setStatus(HttpResponseStatus.FOUND).setHeader(HttpHeaderNames.LOCATION.toString(), uri).send()
    }

    fun sendNotModified(): ChannelFuture? {
        return this.setStatus(HttpResponseStatus.NOT_MODIFIED).setHeader(
            HttpHeaderNames.DATE.toString(), DateUtil.formatHttpDate(
                DateUtil.date()
            )
        ).send()
    }

    fun sendError(status: HttpResponseStatus, msg: String): ChannelFuture? {
        return if (ctx.channel().isActive) {
            this.setStatus(status).setContent(msg).send()
        } else null
    }

    fun sendNotFound(msg: String): ChannelFuture? {
        return sendError(HttpResponseStatus.NOT_FOUND, msg)
    }

    fun sendServerError(msg: String): ChannelFuture? {
        return sendError(HttpResponseStatus.INTERNAL_SERVER_ERROR, msg)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("headers:\r\n ")
        headers.entries()
            .forEach { (key, value) -> sb.append("    ").append(key).append(": ").append(value).append("\r\n") }
        sb.append("content: ").append(StrUtil.str(content, CharsetUtil.UTF_8))
        return sb.toString()
    }

    companion object {
        const val CONTENT_TYPE_TEXT = "text/plain"
        const val CONTENT_TYPE_HTML = "text/html"
        const val CONTENT_TYPE_XML = "text/xml"
        const val CONTENT_TYPE_JAVASCRIPT = "application/javascript"
        const val CONTENT_TYPE_JSON = "application/json"
        const val CONTENT_TYPE_JSON_IE = "text/json"

        fun build(ctx: ChannelHandlerContext, request: ProxyRequest): ProxyResponse {
            return ProxyResponse(ctx, request)
        }
    }
}