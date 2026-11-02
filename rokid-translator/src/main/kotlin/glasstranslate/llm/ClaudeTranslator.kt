package glasstranslate.llm

import glasstranslate.model.Analysis
import glasstranslate.model.Settings
import glasstranslate.model.Speaker
import glasstranslate.model.Turn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/** Anthropic Messages API 기반 구현. 지연이 중요하므로 기본 모델은 Haiku. */
class ClaudeTranslator(
    private val apiKey: String,
    private val model: String = "claude-haiku-5-5",
    private val baseUrl: String = "https://api.anthropic.com",
    private val http: HttpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build(),
) : Translator {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun translate(english: String, history: List<Turn>): String =
        call(
            system = "You are a real-time interpreter. Translate the partner's English into natural, concise Korean. " +
                "Output ONLY the Korean translation, no quotes or notes.",
            user = historyBlock(history) + "Translate: $english",
            maxTokens = 200,
        ).trim()

    override suspend fun suggestReplies(english: String, history: List<Turn>, settings: Settings): Analysis {
        val system = """
            You help a Korean speaker reply in English during a live conversation.
            Situation: ${settings.situation}. Tone: ${settings.tone}.
            ${if (settings.simpleEnglish) "Use simple, short English (CEFR A2-B1)." else ""}
            Return ONLY JSON: {"intent": "<Korean, <=12 chars>", "replies": [{"en": "...", "ko": "<Korean meaning>"}]}
            Give exactly ${settings.maxReplies} replies with different stances (e.g. agree / decline-or-alternative / ask to repeat or clarify).
            Each English reply must be <= 90 characters.
        """.trimIndent()
        val text = call(system, historyBlock(history) + "Partner just said: $english", maxTokens = 500)
        return parseAnalysis(text)
    }

    override suspend fun koreanToEnglish(korean: String, history: List<Turn>, settings: Settings): String =
        call(
            system = "Convert the Korean speaker's intent into natural spoken English. Tone: ${settings.tone}. " +
                "Output ONLY the English sentence.",
            user = historyBlock(history) + "Korean: $korean",
            maxTokens = 200,
        ).trim()

    internal fun parseAnalysis(raw: String): Analysis {
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        if (start < 0 || end <= start) return Analysis()
        return runCatching { json.decodeFromString(Analysis.serializer(), raw.substring(start, end + 1)) }
            .getOrDefault(Analysis())
    }

    private fun historyBlock(history: List<Turn>): String =
        if (history.isEmpty()) "" else
            "Conversation so far:\n" + history.takeLast(6).joinToString("\n") {
                (if (it.speaker == Speaker.PARTNER) "Partner" else "Me") + ": " + it.text
            } + "\n\n"

    private suspend fun call(system: String, user: String, maxTokens: Int): String = withContext(Dispatchers.IO) {
        val body = buildJsonObject {
            put("model", model)
            put("max_tokens", maxTokens)
            put("system", system)
            put("messages", buildJsonArray {
                add(buildJsonObject { put("role", "user"); put("content", user) })
            })
        }
        val req = HttpRequest.newBuilder(URI.create("$baseUrl/v1/messages"))
            .timeout(Duration.ofSeconds(15))
            .header("x-api-key", apiKey)
            .header("anthropic-version", "2023-06-01")
            .header("content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build()
        val res = http.send(req, HttpResponse.BodyHandlers.ofString())
        check(res.statusCode() in 200..299) { "Claude API ${res.statusCode()}: ${res.body().take(200)}" }
        val content: JsonArray = json.parseToJsonElement(res.body()).jsonObject["content"]!!.jsonArray
        content.firstOrNull { (it as JsonObject)["type"]?.jsonPrimitive?.content == "text" }
            ?.jsonObject?.get("text")?.jsonPrimitive?.content.orEmpty()
    }
}
