# NVIDIA × ChatGPT 모션 그래픽 — 디자인 시스템

주제: "챗GPT의 등장과 엔비디아의 주가 폭발"
톤: NVIDIA GTC 키노트 오프닝 시퀀스 — 시네마틱, 다크, 네온 그린, 3D 그리드 공간, GPU 실루엣.

---

## 1. 브랜드 원칙 (NVIDIA CI/BI 기반)

| 원칙 | 설명 |
|---|---|
| **Precision** | 픽셀 그리드 정렬, 수학적 균형. 무작위 없음. |
| **Compute-native** | 데이터/연산이 이미지의 재료. 파티클, 격자, 파형은 계산의 은유. |
| **Cinematic dark** | 검정 위에 정확한 빛. 배경은 대부분 순검정, 강조만 발광. |
| **One green, one moment** | NVIDIA 그린은 히어로 순간에만 최대 명도로. 남발 금지. |

---

## 2. 컬러 시스템

### 2.1 Primary
| 토큰 | HEX | 용도 |
|---|---|---|
| `--nv-green` | `#76B900` | NVIDIA 시그니처. 로고, 핵심 스탯, 상승 라인. |
| `--nv-green-glow` | `#B4FF3A` | 하이라이트/블룸의 하이라이트 코어 (그린을 밝힌 값). |
| `--nv-green-deep` | `#3F6600` | 그린의 그림자/저조도. 그라디언트 하단. |

### 2.2 Neutral (배경/서피스)
| 토큰 | HEX | 용도 |
|---|---|---|
| `--nv-black` | `#000000` | 마스터 배경. |
| `--nv-ink` | `#0A0F0A` | 서피스 1 — 카드/패널 배경 (그린 미묘하게 섞임). |
| `--nv-graphite` | `#14181C` | 서피스 2 — HUD 컨테이너. |
| `--nv-steel` | `#2A2F35` | 디바이더, 그리드 라인 저채도. |
| `--nv-mist` | `#8A929B` | 보조 텍스트. |
| `--nv-white` | `#F5F7F5` | 본문 (완전 흰색 대신 약간 그린 시프트). |

### 2.3 Accent (스토리 전용)
| 토큰 | HEX | 용도 |
|---|---|---|
| `--chatgpt-teal` | `#10A37F` | ChatGPT 아이덴티티. NVIDIA 그린과 반대편 색공간에 배치. |
| `--surge-amber` | `#FFB020` | 주가 급등 임팩트 강조 프레임 (0.3s 미만 플래시). |
| `--warn-red` | `#FF3B30` | 하락/공포 지수. 대비용, 짧게. |

### 2.4 그라디언트 팔레트 (GTC 시그니처)
- `--grad-hero`: `linear-gradient(180deg, #000 0%, #0A0F0A 60%, #14251C 100%)` — 오프닝/타이틀 배경.
- `--grad-green-beam`: `radial-gradient(ellipse at center, #B4FF3A 0%, #76B900 30%, transparent 70%)` — 스팟라이트.
- `--grad-line-chart`: `linear-gradient(90deg, #3F6600 0%, #76B900 50%, #B4FF3A 100%)` — 주가 라인 상승 그라디언트.
- `--grad-data-flow`: `linear-gradient(180deg, rgba(118,185,0,0.0) 0%, rgba(118,185,0,0.6) 50%, rgba(118,185,0,0.0) 100%)` — GPU 다이 위 데이터 스캔 라인.

### 2.5 사용 비율 (60/30/10)
- 60% 순검정 + 그레이 서피스
- 30% 네온 그린 계열 (라인, 웨이브, 리플)
- 10% 화이트/틸/앰버 (텍스트, 스토리 시그널)

---

## 3. 타이포그래피

### 3.1 폰트 스택
| 역할 | 1순위 (NVIDIA CI) | 대체 (오픈 소스) |
|---|---|---|
| Display / 헤드라인 | **NVIDIA Sans Display** | **Inter Display** 또는 **DIN 2014 Bold** |
| Body / UI | **NVIDIA Sans** | **Inter** |
| Mono / 데이터·티커 | **NVIDIA Sans Mono** | **JetBrains Mono** 또는 **IBM Plex Mono** |
| 수치 강조 (스탯 카운트업) | NVIDIA Sans Mono, `font-variant-numeric: tabular-nums` | 동일 |

폰트 라이선스가 확보되지 않은 경우 Inter/JetBrains Mono로 진행하되, 트래킹·웨이트를 아래 표대로 맞추면 GTC 느낌이 유지됩니다.

### 3.2 스케일 (1920×1080 기준, 유닛 = px)
| 토큰 | 크기 | line-height | letter-spacing | weight | 용도 |
|---|---|---|---|---|---|
| `--fs-hero` | 168 | 0.92 | -0.02em | 700 | 오프닝 타이틀 한 줄 |
| `--fs-headline` | 96 | 1.00 | -0.01em | 700 | 챕터 헤드 |
| `--fs-sub` | 48 | 1.15 | 0 | 500 | 서브 라인 |
| `--fs-stat` | 240 | 0.88 | -0.03em | 700 | 카운트업 수치 (예: +1,200%) |
| `--fs-label` | 22 | 1.2 | 0.18em (uppercase) | 600 | 케이오티드 라벨: "MARKET CAP" |
| `--fs-ticker` | 28 | 1.2 | 0.04em | 500 | NVDA 티커, 타임스탬프 |
| `--fs-caption` | 20 | 1.4 | 0 | 400 | 하단 주석 |

### 3.3 규칙
- **대문자 라벨**: 모든 카테고리/축 라벨은 UPPERCASE + 넓은 트래킹. GTC HUD 시그니처.
- **수치는 모노**: 카운트업, 티커, 좌표계는 tabular-nums. 흔들림 없음.
- **한 프레임 = 한 아이디어**: 헤드라인은 최대 6단어. 두 줄까지 허용.
- **위계 3단**: display / label / caption 세 층만 동시 노출.

---

## 4. 그리드 & 레이아웃

### 4.1 캔버스
- 마스터: **1920×1080 (16:9)**, 소셜 컷다운 시 1080×1920 (9:16) 재프레이밍.
- 세이프존: 상하좌우 각 96px (5%).
- 컬럼 그리드: **12 col**, gutter 32px, side margin 96px.

### 4.2 GTC HUD 오버레이 (선택)
- 좌상단: `[NVDA · REALTIME]` 라벨 + 얇은 진행 바.
- 우상단: 타임스탬프 `2022.11 → 2024.06` 진행 표시.
- 하단 좌: 얇은 브랜드 록업 라인 `NVIDIA` 로고 + 얇은 세로 룰 + 챕터 넘버 `01 / 04`.
- 하단 우: 데이터 소스 캡션 (`SOURCE: NASDAQ`).

### 4.3 3D 씬 공간
- **원점(0,0,0)**은 화면 정중앙. 카메라는 항상 원점을 향함.
- 그리드 플로어: 검정 위에 `--nv-steel` 라인 (opacity 0.3), 원근 소실점 화면 상단 60% 지점.
- 후경 파티클: 그린 도트 400개, 크기 1~3px, opacity 0.15~0.4, Z축 파랄락스.

---

## 5. 아이코노그래피 & 그래픽 프리미티브

| 요소 | 스펙 |
|---|---|
| **선 굵기** | 1px (얇은 HUD), 2px (기본), 4px (히어로 라인/차트 라인). |
| **모서리 반경** | 카드 12px, 버튼/태그 4px, 다이(die) 12px. |
| **화살표** | 삼각 헤드 12×16, 라인은 subject와 동일 굵기. |
| **아이콘** | 스트로크 스타일 (fill 없음), 각 1.5px 스트로크, 24/32/48 그리드. |
| **GPU 다이 실루엣** | 정사각형 96px 그리드 위 12×12 셀 미세 격자, `--nv-green-deep` 라인. GTC 오프닝 시그니처. |
| **로고 록업** | 항상 `--nv-green` 100% 명도. 배경 위 대비 4.5:1 이상 유지. |

---

## 6. 모션 원칙 (GTC 톤)

### 6.1 이징 라이브러리 (GSAP 기준)
| 토큰 | 이징 | 용도 |
|---|---|---|
| `--ease-precision` | `power3.out` | 텍스트 등장, 클린한 정지. |
| `--ease-compute` | `expo.out` | 데이터/파티클의 급가속 → 정지. |
| `--ease-drift` | `sine.inOut` | 배경 그리드, 카메라 드리프트. |
| `--ease-punch` | `back.out(1.7)` | 스탯 히트 순간 (오버슛). |

### 6.2 타이밍 표
| 이벤트 | 지속 | 노트 |
|---|---|---|
| 텍스트 마스크 리빌 | 0.6–0.9s | 위→아래 클립 마스크, blur 0→0. |
| 스탯 카운트업 | 1.2–1.8s | `expo.out`, 마지막 200ms에 pulse. |
| 차트 라인 드로우 | 1.4–2.0s | strokeDashoffset, `power2.inOut`. |
| 카메라 컷 | 즉시 | 페이드/디졸브 지양. 하드 컷 = GTC. |
| 카메라 이동 | 2.5–4.0s | 항상 `--ease-drift`. 흔들림 금지. |
| 씬 전환 (트랜지션) | 0.4s | 화이트 or 그린 플래시 1프레임 → 다음 씬. |

### 6.3 시그니처 모션
- **Grid rise**: 플로어 그리드가 원점에서 밖으로 팽창하며 등장 (`scale` + `opacity`).
- **Data scan**: 그린 스캔 라인이 GPU 다이 위를 위→아래 통과. 통과 자리에 격자가 활성화됨.
- **Compute burst**: 원점에서 그린 파티클이 방사형으로 분출 → 다시 원점으로 수렴하며 로고 형태로 조립.
- **Chart surge**: 라인이 좌하단에서 우상단으로 스트로크 → 정점에서 파티클 튀김 + 앰버 플래시.
- **Text lockup**: 대문자 라벨이 좌측에서 슬라이드 인, 그 아래 히어로 텍스트가 마스크 리빌.

### 6.4 금기
- 컬러풀 그라디언트 배경 회전 ❌ (GTC 아님)
- 이징 없는 리니어 애니메이션 ❌
- 3D 카메라의 손흔들림 시뮬레이션 ❌
- 그린 100%를 5초 이상 동시에 다수 요소에 사용 ❌

---

## 7. 후처리 & 이펙트

| 이펙트 | 스펙 | 사용처 |
|---|---|---|
| **Bloom** | radius 24px, threshold 0.7, intensity 0.6 | 그린 라인/스탯 발광. |
| **Chromatic aberration** | R/B offset 1.5px | 히어로 임팩트 프레임 200ms만. |
| **Film grain** | 3% 강도, monochrome | 항상 은은하게. GTC의 시네마 질감. |
| **Vignette** | 20% 어둡게, edge only | 마스터 프레임 전체. |
| **Scan lines** | opacity 0.04, 1px, 2px 간격 | 데이터 뷰 씬에서만. |

블룸은 라이트웨이트 SVG filter 조합으로 시작하고, 필요시 Three.js `UnrealBloomPass`로 승격.

---

## 8. 스토리 → 시각 언어 매핑 (참고 씬 리스트)

| 씬 | 서사 | 핵심 시각 | 지속 |
|---|---|---|---|
| 01. Cold open | "2022.11 — 한 대화가 시작됐다." | 검정 위 커서 깜빡임 → ChatGPT 틸 라인 등장 → 라인이 폭발하며 그린 파티클로 전환 | 3.0s |
| 02. Compute | "AI의 심장은 GPU다." | GPU 다이 실루엣, data scan, 그리드 활성화 | 4.0s |
| 03. Surge | "NVDA — 500%↑" | 주가 라인 드로우 + 카운트업 스탯 + 앰버 임팩트 | 3.5s |
| 04. Market cap | "$1T → $3T" | 두 개의 거대 수치가 좌우로 서서 비교, 사이에 상승 화살표 | 3.0s |
| 05. Lockup | "The AI Era, Computed." | 파티클이 NVIDIA 로고로 수렴 | 2.5s |

총 러닝 ~16초. 모션 그래픽 스킬의 "under 10s" 범위를 넘으므로 실제 빌드 단계에서는 `/general-video`로 라우팅될 가능성 큼 — 결정은 인텐트 인터뷰에서 확정.

---

## 9. 토큰 요약 (빌드 시 CSS 변수로)

```css
:root {
  /* color */
  --nv-green: #76B900;
  --nv-green-glow: #B4FF3A;
  --nv-green-deep: #3F6600;
  --nv-black: #000000;
  --nv-ink: #0A0F0A;
  --nv-graphite: #14181C;
  --nv-steel: #2A2F35;
  --nv-mist: #8A929B;
  --nv-white: #F5F7F5;
  --chatgpt-teal: #10A37F;
  --surge-amber: #FFB020;
  --warn-red: #FF3B30;

  /* type */
  --font-display: "NVIDIA Sans Display", "Inter Display", system-ui, sans-serif;
  --font-body: "NVIDIA Sans", "Inter", system-ui, sans-serif;
  --font-mono: "NVIDIA Sans Mono", "JetBrains Mono", monospace;

  /* motion */
  --ease-precision: power3.out;
  --ease-compute: expo.out;
  --ease-drift: sine.inOut;
  --ease-punch: back.out(1.7);
}
```

---

## 10. 라이선스·리스크 노트

- **NVIDIA 로고 & 워드마크**: 상용 프로젝트에서는 사전 승인 필요. 학습/포트폴리오는 fair use 범위. 로고는 항상 공식 SVG 원본 사용, 재작도 금지.
- **NVIDIA Sans**: 자사 폰트, 외부 배포 금지. Inter/JetBrains Mono로 안전하게 대체 가능.
- **GTC "느낌"**: 저작권 대상 아님. 특정 GTC 영상 컷을 재현하지 말고, 위의 원칙을 조합해 자체 씬으로 구성.
- **ChatGPT 로고**: OpenAI 상표. 실루엣/이니셜/타이포로 표현하는 것이 안전.
- **주가 차트**: 표시 값은 실측 데이터를 사용하되, "예시" 명시 또는 SOURCE 캡션 필수.
