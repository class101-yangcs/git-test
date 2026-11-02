package glasstranslate.llm

import glasstranslate.model.Analysis
import glasstranslate.model.Reply
import glasstranslate.model.Settings
import glasstranslate.model.Turn
import kotlinx.coroutines.delay

/** 모의 시험 시나리오 대사: 영어 → (한국어 번역, 영어 답변 후보). */
object DemoScript {
    private fun r(en: String, ko: String) = Reply(en, ko)
    private val again = r("Sorry, could you say that again?", "죄송해요, 다시 말씀해 주시겠어요?")

    val lines: Map<String, Pair<String, List<Reply>>> = mapOf(
        "Hi there! What can I get for you today?" to ("안녕하세요! 오늘 뭐 드릴까요?" to listOf(
            r("Can I get an iced americano, please?", "아이스 아메리카노 한 잔 주시겠어요?"),
            r("What do you recommend?", "추천해 주실 만한 게 있나요?"), again)),
        "Would you like that for here or to go?" to ("여기서 드시겠어요, 포장하시겠어요?" to listOf(
            r("For here, please.", "여기서 먹을게요."), r("To go, please.", "포장해 주세요."), again)),
        "That'll be four fifty. Cash or card?" to ("4달러 50센트입니다. 현금이세요, 카드세요?" to listOf(
            r("Card, please.", "카드로 할게요."), r("I'll pay in cash.", "현금으로 낼게요."), again)),
        "Are you free to meet tomorrow afternoon?" to ("내일 오후에 만날 시간 되세요?" to listOf(
            r("Sure, what time works for you?", "네, 몇 시가 좋으세요?"),
            r("Sorry, I'm busy. How about Thursday?", "죄송해요, 바빠요. 목요일은 어때요?"), again)),
        "How about three o'clock?" to ("3시는 어때요?" to listOf(
            r("Three o'clock works for me.", "3시 좋아요."),
            r("Could we make it four instead?", "4시로 할 수 있을까요?"), again)),
        "Great, I'll send you a calendar invite." to ("좋아요, 캘린더 초대를 보내 드릴게요." to listOf(
            r("Thanks, I'll watch for it.", "고마워요, 확인할게요."),
            r("Could you also add the address?", "주소도 같이 넣어 주실래요?"), again)),
        "Excuse me, do you know how to get to the subway station?" to ("실례합니다, 지하철역에 어떻게 가는지 아세요?" to listOf(
            r("Sure! Go straight and turn left.", "그럼요! 직진하다가 왼쪽으로 도세요."),
            r("Sorry, I'm not from here.", "죄송해요, 저도 이 동네 사람이 아니라서요."), again)),
        "Is it far from here?" to ("여기서 먼가요?" to listOf(
            r("No, it's about five minutes on foot.", "아니요, 걸어서 5분쯤이에요."),
            r("It's a bit far. You can take a bus.", "좀 멀어요. 버스를 타시면 돼요."), again)),
    )
    val koToEn: Map<String, String> = mapOf(
        "목요일은 어때요?" to "How about Thursday?",
        "카드로 할게요" to "I'll pay by card, please.",
        "다시 말씀해 주세요" to "Could you say that again, please?",
    )
}

/** API 키 없이 파이프라인/시뮬레이터를 시험하기 위한 가짜 구현. latency는 LLM 지연을 흉내 낸다. */
class FakeTranslator(
    private val translateDelayMs: Long = 0,
    private val repliesDelayMs: Long = 0,
) : Translator {
    override suspend fun translate(english: String, history: List<Turn>): String {
        delay(translateDelayMs)
        return DemoScript.lines[english]?.first ?: "[번역] $english"
    }

    override suspend fun suggestReplies(english: String, history: List<Turn>, settings: Settings): Analysis {
        delay(repliesDelayMs)
        val known = DemoScript.lines[english]?.second
        return Analysis(
            intent = "데모",
            replies = (known ?: listOf(
                Reply("Sure, sounds good.", "네, 좋아요."),
                Reply("Sorry, I can't. Maybe later?", "죄송해요, 어려워요. 나중에요?"),
                Reply("Could you say that again?", "다시 말씀해 주시겠어요?"),
            )).take(settings.maxReplies),
        )
    }

    override suspend fun koreanToEnglish(korean: String, history: List<Turn>, settings: Settings): String {
        delay(translateDelayMs)
        return DemoScript.koToEn[korean.trim()] ?: "[EN] $korean"
    }
}
