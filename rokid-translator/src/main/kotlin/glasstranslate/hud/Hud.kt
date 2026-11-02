package glasstranslate.hud

import glasstranslate.model.Reply

/** 글래스에 보낼 한 화면(프레임). 실제 Rokid SDK 출력 어댑터가 이것을 렌더링한다. */
data class HudFrame(val lines: List<String>)

interface GlassesDisplay {
    fun show(frame: HudFrame)
}

/** 작은 단색 HUD용 레이아웃: 줄 수/너비 제한과 한글 폭 고려. */
class HudFormatter(
    private val maxCols: Int = 22,       // 한 줄 폭(전각 한글=2칸으로 계산)
    private val maxSubtitleLines: Int = 2,
) {
    fun subtitle(korean: String, partial: Boolean = false): HudFrame {
        val lines = wrap(korean, maxCols)
        val shown = if (lines.size > maxSubtitleLines) lines.takeLast(maxSubtitleLines) else lines
        return HudFrame(if (partial) shown.dropLast(1) + (shown.last() + "…") else shown)
    }

    fun replies(korean: String, replies: List<Reply>): HudFrame {
        val out = mutableListOf<String>()
        out += wrap(korean, maxCols).take(maxSubtitleLines)
        out += "──────"
        replies.forEachIndexed { i, r -> out += wrap("${i + 1}. ${r.en}", maxCols).take(2) }
        return HudFrame(out)
    }

    fun selected(reply: Reply): HudFrame =
        HudFrame(wrap(reply.en, maxCols).take(3) + wrap(reply.ko, maxCols).take(2))

    /** 표시 폭 기준 줄바꿈. 영어는 단어 경계, 한글은 글자 단위로 자른다. */
    fun wrap(text: String, cols: Int): List<String> {
        val lines = mutableListOf<String>()
        var cur = StringBuilder()
        var width = 0
        fun flush() { if (cur.isNotEmpty()) lines += cur.toString().trimEnd(); cur = StringBuilder(); width = 0 }
        for (token in tokenize(text)) {
            val w = displayWidth(token)
            if (width + w > cols && cur.isNotEmpty()) flush()
            if (token.isBlank() && cur.isEmpty()) continue
            cur.append(token); width += w
        }
        flush()
        return lines.ifEmpty { listOf("") }
    }

    private fun tokenize(text: String): List<String> {
        val tokens = mutableListOf<String>()
        val word = StringBuilder()
        fun flushWord() { if (word.isNotEmpty()) { tokens += word.toString(); word.clear() } }
        for (ch in text) {
            when {
                ch == ' ' -> { flushWord(); tokens += " " }
                isWide(ch) -> { flushWord(); tokens += ch.toString() }
                else -> word.append(ch)
            }
        }
        flushWord()
        return tokens
    }

    companion object {
        fun isWide(ch: Char): Boolean = ch.code in 0x1100..0x11FF || ch.code in 0x3130..0x318F || ch.code in 0xAC00..0xD7A3
        fun displayWidth(s: String): Int = s.sumOf { if (isWide(it)) 2 else 1 as Int }
    }
}

/** 콘솔 데모/테스트용 디스플레이. */
class ConsoleDisplay : GlassesDisplay {
    override fun show(frame: HudFrame) {
        println("┌────────── HUD ──────────")
        frame.lines.forEach { println("│ $it") }
        println("└─────────────────────────")
    }
}
