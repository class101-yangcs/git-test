# rokid-translator

Rokid Glasses용 실시간 통역·답변 보조의 **코어 파이프라인** (Kotlin/JVM).

영어 음성(STT) → 한국어 자막 즉시 표시 → 영어 답변 후보 3개를 뒤따라 표시 → 터치로 선택,
또는 한국어로 말하면 영어로 변환.

## 구조
| 패키지 | 역할 | 실기기 연동 시 교체 대상 |
|---|---|---|
| `stt/SttEngine` | 영어 음성 → 텍스트 이벤트 스트림 | 글래스 마이크 오디오 + 스트리밍 STT |
| `llm/Translator` | 번역 / 답변 제안 / 한→영 | `ClaudeTranslator` 제공 (Anthropic Messages API) |
| `hud/HudFormatter`, `GlassesDisplay` | 작은 HUD용 줄바꿈(한글 2칸) 및 프레임 | Rokid SDK 출력 어댑터 |
| `pipeline/ConversationPipeline` | 오케스트레이션 (자막 먼저, 답변은 비동기, 새 발화 시 이전 답변 취소) | 그대로 사용 |

## 실행 / 테스트
```bash
gradle test
ANTHROPIC_API_KEY=... gradle run -q --console=plain   # 키 없으면 FakeTranslator
```
데모 입력: 영어 한 줄 = 상대 발화, `/1`~`/3` = 답변 선택, `/ko 문장` = 한국어→영어.
`CLAUDE_MODEL` 환경변수로 모델 변경(기본 `claude-haiku-5-5`, 지연 우선).

## 아직 안 된 것 (다음 단계)
- Rokid SDK 오디오 수신/HUD 출력 어댑터 (SDK 문서·실기기 필요, Android 앱 모듈로 분리 예정)
- 실제 스트리밍 STT 구현체, 화자 분리(내 목소리 제외), VAD
- 부분(partial) 결과 번역, 오프라인 폴백, 대화 로그 저장
