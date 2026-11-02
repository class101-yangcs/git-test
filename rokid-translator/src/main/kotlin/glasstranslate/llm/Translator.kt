package glasstranslate.llm

import glasstranslate.model.Analysis
import glasstranslate.model.Settings
import glasstranslate.model.Turn

interface Translator {
    /** 영어 → 한국어 번역. 빠르게 반환해야 한다(자막용). */
    suspend fun translate(english: String, history: List<Turn>): String

    /** 맥락을 고려한 의도 파악 + 영어 답변 후보. 번역과 별도로 비동기 호출한다. */
    suspend fun suggestReplies(english: String, history: List<Turn>, settings: Settings): Analysis

    /** 사용자의 한국어 발화 → 자연스러운 영어. */
    suspend fun koreanToEnglish(korean: String, history: List<Turn>, settings: Settings): String
}
