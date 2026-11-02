package glasstranslate

import glasstranslate.hud.GlassesDisplay
import glasstranslate.hud.HudFormatter
import glasstranslate.hud.HudFrame
import glasstranslate.llm.ClaudeTranslator
import glasstranslate.llm.FakeTranslator
import glasstranslate.model.Speaker
import glasstranslate.pipeline.ConversationPipeline
import glasstranslate.stt.SttEngine
import glasstranslate.stt.SttEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PipelineTest {
    private class ListDisplay : GlassesDisplay {
        val frames = mutableListOf<HudFrame>()
        override fun show(frame: HudFrame) { frames += frame }
    }
    private class ScriptedStt(vararg val events: SttEvent) : SttEngine {
        override fun events(): Flow<SttEvent> = flowOf(*events)
    }

    @Test fun `subtitle shown first then replies`() = runTest {
        val display = ListDisplay()
        val p = ConversationPipeline(ScriptedStt(SttEvent.Final("Are you free tomorrow?")), FakeTranslator(), display)
        p.run()
        assertEquals(2, display.frames.size)
        assertTrue(display.frames[0].lines.first().startsWith("[번역]"))
        assertTrue(display.frames[1].lines.any { it.startsWith("1. ") })
        assertEquals(3, p.currentReplies.size)
    }

    @Test fun `select records my turn and shows english`() = runTest {
        val display = ListDisplay()
        val p = ConversationPipeline(ScriptedStt(SttEvent.Final("Hi")), FakeTranslator(), display)
        p.run()
        val r = p.select(2)!!
        assertEquals(Speaker.ME, p.history().last().speaker)
        assertEquals(r.en, p.history().last().text)
        assertNull(p.select(9))
    }

    @Test fun `partial events are ignored`() = runTest {
        val display = ListDisplay()
        ConversationPipeline(ScriptedStt(SttEvent.Partial("Are yo")), FakeTranslator(), display).run()
        assertTrue(display.frames.isEmpty())
    }

    @Test fun `hud wraps korean by display width`() {
        val f = HudFormatter(maxCols = 10)
        val lines = f.wrap("내일 회의 시간 괜찮으세요", 10)
        assertTrue(lines.all { HudFormatter.displayWidth(it) <= 10 }, lines.toString())
    }

    @Test fun `hud wraps english on word boundaries`() {
        val lines = HudFormatter().wrap("Sorry, I'm booked tomorrow. How about Thursday?", 22)
        assertTrue(lines.all { it.length <= 22 }, lines.toString())
        assertEquals("Sorry, I'm booked", lines.first())
    }

    @Test fun `parse analysis tolerates surrounding text`() {
        val t = ClaudeTranslator("k")
        val a = t.parseAnalysis("""Here: {"intent":"일정","replies":[{"en":"Sure","ko":"네"}]} done""")
        assertEquals("일정", a.intent)
        assertEquals("Sure", a.replies.single().en)
        assertTrue(t.parseAnalysis("garbage").replies.isEmpty())
    }
}
