# Project notes

## Active design system

Motion graphics work in this repo follows the design system at:

- [`videos/nvidia-chatgpt-motion/design.md`](videos/nvidia-chatgpt-motion/design.md)

Subject: "챗GPT의 등장과 엔비디아의 주가 폭발" motion graphic, NVIDIA CI/BI + GTC tone.

When building any scene, storyboard frame, HTML composition, or asset for
this project, **read `design.md` first** and use its tokens verbatim:

- Colors: `--nv-green` `#76B900`, `--nv-green-glow` `#B4FF3A`, `--nv-black` `#000000`, `--chatgpt-teal` `#10A37F`, `--surge-amber` `#FFB020`, full palette in §2.
- Type: NVIDIA Sans stack (fallback Inter / JetBrains Mono); scale in §3.2; UPPERCASE + 0.18em tracking for labels.
- Layout: 1920×1080, 12-col, 96px safe zone; GTC HUD (ticker top-left, timestamp top-right, brand + chapter bottom).
- Motion: eases `power3.out` / `expo.out` / `sine.inOut` / `back.out(1.7)`; signature moves Grid rise / Data scan / Compute burst / Chart surge / Text lockup; hard cuts + 1-frame flash, no dissolves.
- Effects: bloom on green, 3% film grain, vignette, 200 ms chromatic aberration on impact.
- Story map: 5 scenes, ~16s total (see §8).

Do not introduce colors, fonts, easings, or transitions outside the design system without updating `design.md` first.
