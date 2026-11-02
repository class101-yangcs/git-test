package glasstranslate.pipeline

import glasstranslate.hud.GlassesDisplay
import glasstranslate.hud.HudFormatter
import glasstranslate.llm.Translator
import glasstranslate.model.Reply
import glasstranslate.model.Settings
import glasstranslate.model.Speaker
import glasstranslate.model.Turn
import glasstranslate.stt.SttEngine
import glasstranslate.stt.SttEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * 영어 듣기 → 한국어 자막 즉시 표시 → 답변 후보는 뒤따라 비동기로 표시.
 * 새 발화가 오면 이전 답변 생성은 취소한다(오래된 제안이 화면을 덮지 않도록).
 */
class ConversationPipeline(
    private val stt: SttEngine,
    private val translator: Translator,
    private val display: GlassesDisplay,
    private val settings: Settings = Settings(),
    private val formatter: HudFormatter = HudFormatter(),
) {
    private val history = mutableListOf<Turn>()
    @Volatile var currentReplies: List<Reply> = emptyList(); private set

    fun history(): List<Turn> = history.toList()

    suspend fun run() = coroutineScope {
        var repliesJob: Job? = null
        stt.events().collect { ev ->
            when (ev) {
                is SttEvent.Partial -> Unit // 부분 번역은 비용/깜빡임 때문에 MVP에선 생략
                is SttEvent.Final -> {
                    repliesJob?.cancelAndJoin()
                    repliesJob = launch { handleFinal(ev.text) }
                }
            }
        }
        repliesJob?.join()
        Unit
    }

    private suspend fun handleFinal(english: String) = coroutineScope {
        val snapshot = history.toList()
        history += Turn(Speaker.PARTNER, english)
        currentReplies = emptyList()

        // 번역과 답변 생성을 동시에 시작하되, 자막은 번역이 끝나는 즉시 표시.
        val repliesDeferred = async { runCatching { translator.suggestReplies(english, snapshot, settings) } }
        val korean = runCatching { translator.translate(english, snapshot) }
            .getOrElse { "(번역 실패)" }
        display.show(formatter.subtitle(korean))

        val analysis = repliesDeferred.await().getOrNull()
        if (analysis != null && analysis.replies.isNotEmpty()) {
            currentReplies = analysis.replies.take(settings.maxReplies)
            display.show(formatter.replies(korean, currentReplies))
        }
    }

    /** 사용자가 터치패드로 n번(1부터) 답변을 고름. */
    fun select(n: Int): Reply? {
        val reply = currentReplies.getOrNull(n - 1) ?: return null
        history += Turn(Speaker.ME, reply.en)
        display.show(formatter.selected(reply))
        return reply
    }

    /** 사용자가 한국어로 말한 내용을 영어로 변환해 표시. */
    suspend fun speakKorean(korean: String): String {
        val en = translator.koreanToEnglish(korean, history.toList(), settings)
        history += Turn(Speaker.ME, en)
        display.show(formatter.selected(Reply(en, korean)))
        return en
    }
}
