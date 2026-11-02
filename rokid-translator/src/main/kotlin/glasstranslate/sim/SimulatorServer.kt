package glasstranslate.sim

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import glasstranslate.hud.GlassesDisplay
import glasstranslate.hud.HudFrame
import glasstranslate.llm.Translator
import glasstranslate.pipeline.ConversationPipeline
import glasstranslate.stt.SttEngine
import glasstranslate.stt.SttEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.IOException
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

/**
 * 컴퓨터 안에서 모의 시험하기 위한 로컬 시뮬레이터.
 * 브라우저가 "글래스(마이크+HUD)" 역할을 하고, 서버는 실제 ConversationPipeline을 그대로 돌린다.
 *   GET  /            시뮬레이터 화면
 *   GET  /events      SSE (hud / heard / partial 이벤트)
 *   POST /say?text=   상대가 한 문장 말함(STT 확정)
 *   POST /partial?text= 인식 중 부분 결과(화면 표시용)
 *   POST /select?n=   답변 후보 선택
 *   POST /ko?text=    한국어 → 영어
 */
class SimulatorServer(private val port: Int, translator: Translator) {
    private val clients = CopyOnWriteArrayList<OutputStream>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val sttChannel = Channel<SttEvent>(Channel.UNLIMITED)
    @Volatile private var lastInputAt = System.nanoTime()

    private val display = object : GlassesDisplay {
        override fun show(frame: HudFrame) {
            val kind = if (frame.lines.any { it.startsWith("──") }) "replies" else "subtitle"
            broadcast("hud", buildJsonObject {
                put("kind", kind)
                put("ms", (System.nanoTime() - lastInputAt) / 1_000_000)
                put("lines", buildJsonArray { frame.lines.forEach { add(kotlinx.serialization.json.JsonPrimitive(it)) } })
            }.toString())
        }
    }
    private val stt = object : SttEngine { override fun events(): Flow<SttEvent> = sttChannel.receiveAsFlow() }
    private val pipeline = ConversationPipeline(stt, translator, display)

    private fun broadcast(event: String, data: String) {
        val bytes = "event: $event\ndata: $data\n\n".toByteArray()
        for (c in clients) try { c.write(bytes); c.flush() } catch (_: IOException) { clients.remove(c) }
    }

    private fun text(ex: HttpExchange, key: String): String =
        (ex.requestURI.rawQuery ?: "").split("&").mapNotNull { it.split("=", limit = 2).takeIf { p -> p.size == 2 } }
            .firstOrNull { it[0] == key }?.let { URLDecoder.decode(it[1], Charsets.UTF_8) }.orEmpty()

    private fun reply(ex: HttpExchange, code: Int, body: String, type: String = "application/json; charset=utf-8") {
        val b = body.toByteArray(Charsets.UTF_8)
        ex.responseHeaders.add("Content-Type", type)
        ex.sendResponseHeaders(code, b.size.toLong()); ex.responseBody.use { it.write(b) }
    }

    fun start() {
        scope.launch { pipeline.run() }
        val server = HttpServer.create(InetSocketAddress("127.0.0.1", port), 0)
        server.executor = Executors.newCachedThreadPool()
        server.createContext("/") { ex ->
            val html = javaClass.getResourceAsStream("/sim/index.html")?.readBytes()
            if (html == null) reply(ex, 404, "not found", "text/plain") else reply(ex, 200, String(html), "text/html; charset=utf-8")
        }
        server.createContext("/events") { ex ->
            ex.responseHeaders.add("Content-Type", "text/event-stream")
            ex.responseHeaders.add("Cache-Control", "no-cache")
            ex.sendResponseHeaders(200, 0)
            ex.responseBody.write(": connected\n\n".toByteArray()); ex.responseBody.flush()
            clients += ex.responseBody
        }
        server.createContext("/say") { ex ->
            val t = text(ex, "text").trim()
            if (t.isNotEmpty()) {
                lastInputAt = System.nanoTime()
                broadcast("heard", buildJsonObject { put("text", t) }.toString())
                sttChannel.trySend(SttEvent.Final(t))
            }
            reply(ex, 200, "{}")
        }
        server.createContext("/partial") { ex ->
            broadcast("partial", buildJsonObject { put("text", text(ex, "text")) }.toString()); reply(ex, 200, "{}")
        }
        server.createContext("/select") { ex ->
            lastInputAt = System.nanoTime()
            val r = pipeline.select(text(ex, "n").toIntOrNull() ?: 0)
            reply(ex, 200, buildJsonObject { put("ok", r != null) }.toString())
        }
        server.createContext("/ko") { ex ->
            lastInputAt = System.nanoTime()
            val ko = text(ex, "text").trim()
            val en = if (ko.isEmpty()) "" else runBlocking { pipeline.speakKorean(ko) }
            reply(ex, 200, buildJsonObject { put("en", en) }.toString())
        }
        server.start()
        println("모의 시험 시뮬레이터: http://localhost:$port  (Ctrl+C로 종료)")
    }
}
