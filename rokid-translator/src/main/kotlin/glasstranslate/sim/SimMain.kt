package glasstranslate.sim

import glasstranslate.llm.ClaudeTranslator
import glasstranslate.llm.FakeTranslator

/** 시뮬레이터 실행. API 키가 있으면 실제 Claude, 없으면 지연을 흉내 낸 FakeTranslator. */
fun main() {
    val key = System.getenv("ANTHROPIC_API_KEY")
    val translator = if (key.isNullOrBlank()) {
        println("(ANTHROPIC_API_KEY 없음 → 데모 대본 + 지연 흉내 FakeTranslator)")
        FakeTranslator(translateDelayMs = 350, repliesDelayMs = 900)
    } else ClaudeTranslator(key, model = System.getenv("CLAUDE_MODEL") ?: "claude-haiku-5-5")
    SimulatorServer(System.getenv("PORT")?.toIntOrNull() ?: 8080, translator).start()
    Thread.currentThread().join()
}
