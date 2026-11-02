package glasstranslate.model

import kotlinx.serialization.Serializable

/** 한 번의 상대 발화에 대한 분석 결과. */
@Serializable
data class Reply(val en: String, val ko: String)

@Serializable
data class Analysis(
    val intent: String = "",
    val replies: List<Reply> = emptyList(),
)

/** 대화 히스토리 한 턴. */
data class Turn(val speaker: Speaker, val text: String)

enum class Speaker { PARTNER, ME }

/** 사용자 설정: 상황/말투/난이도. */
data class Settings(
    val situation: String = "일상 대화",
    val tone: String = "공손하고 자연스럽게",
    val simpleEnglish: Boolean = true,
    val maxReplies: Int = 3,
)
