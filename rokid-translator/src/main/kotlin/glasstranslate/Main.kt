package glasstranslate

import glasstranslate.hud.ConsoleDisplay
import glasstranslate.llm.ClaudeTranslator
import glasstranslate.llm.FakeTranslator
import glasstranslate.pipeline.ConversationPipeline
import glasstranslate.stt.SttEngine
import glasstranslate.stt.SttEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import java.io.PrintStream

/**
 * 콘솔 데모. 표준입력의 영어 한 줄 = 상대방이 한 문장 말한 것(STT 확정 결과)으로 취급한다.
 *   - "/1" "/2" "/3": 답변 후보 선택
 *   - "/ko 문장": 한국어 → 영어 변환
 * ANTHROPIC_API_KEY가 없으면 FakeTranslator로 동작한다.
 */
private class StdinStt(private val onCommand: suspend (String) -> Unit) : SttEngine {
    private val reader = System.`in`.bufferedReader(Charsets.UTF_8)

    override fun events(): Flow<SttEvent> = flow {
        while (true) {
            val line = reader.readLine()?.trim() ?: break
            when {
                line.isEmpty() -> Unit
                line.startsWith("/") -> { delay(300); onCommand(line) } // 데모: 답변 후보가 뜰 시간을 줌
                else -> emit(SttEvent.Final(line))
            }
        }
    }
}

fun main(): Unit = runBlocking {
    System.setOut(PrintStream(System.out, true, Charsets.UTF_8))
    val key = System.getenv("ANTHROPIC_API_KEY")
    val translator = if (key.isNullOrBlank()) {
        println("(ANTHROPIC_API_KEY 없음 → FakeTranslator 사용)")
        FakeTranslator()
    } else ClaudeTranslator(key, model = System.getenv("CLAUDE_MODEL") ?: "claude-haiku-5-5")

    lateinit var pipeline: ConversationPipeline
    val stt = StdinStt { cmd ->
        when {
            cmd.matches(Regex("/[1-9]")) -> pipeline.select(cmd.drop(1).toInt())
            cmd.startsWith("/ko ") -> pipeline.speakKorean(cmd.removePrefix("/ko "))
        }
    }
    pipeline = ConversationPipeline(stt, translator, ConsoleDisplay())
    println("상대방의 영어 문장을 입력하세요. (/1~/3 선택, /ko 한국어문장)")
    pipeline.run()
}
