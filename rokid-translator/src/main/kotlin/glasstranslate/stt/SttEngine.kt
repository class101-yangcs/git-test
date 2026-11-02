package glasstranslate.stt

import kotlinx.coroutines.flow.Flow

sealed interface SttEvent {
    /** 인식 중인 부분 결과(문장 미완성). */
    data class Partial(val text: String) : SttEvent
    /** 문장 확정. */
    data class Final(val text: String) : SttEvent
}

/**
 * 영어 음성 → 텍스트 스트림.
 * 실제 구현은 글래스 마이크 오디오(SDK) + 스트리밍 STT(Deepgram/Google/Whisper 등)로 교체한다.
 */
interface SttEngine {
    fun events(): Flow<SttEvent>
}
