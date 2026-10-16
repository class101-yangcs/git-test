# 입주 전 실행과제 관리

`index.html` 한 장짜리 웹페이지입니다. 로그인 없이 열 수 있고, 메모와 과제 상태는 Firebase Realtime Database에 저장되어 모두가 같은 내용을 봅니다.

## 설정 (처음 한 번)

1. [Firebase 콘솔](https://console.firebase.google.com)에서 프로젝트를 만듭니다.
2. **빌드 → Realtime Database → 데이터베이스 만들기**로 DB를 만듭니다.
3. **규칙** 탭에 아래를 붙여넣고 게시합니다.
   ```json
   {
     "rules": {
       "tasks": { ".read": true, ".write": true },
       "memos": { ".read": true, ".write": true }
     }
   }
   ```
4. DB 화면 상단의 주소(`https://…firebasedatabase.app` 또는 `https://…firebaseio.com`)를 복사해 `index.html`의 `FIREBASE_URL`에 붙여넣습니다.
5. GitHub Pages(Settings → Pages)로 게시하면 누구나 링크로 열 수 있습니다.

## 알아둘 점

- 링크를 아는 사람은 누구나 메모를 쓰고 지울 수 있습니다. 링크를 외부에 공개하지 마세요.
- `FIREBASE_URL`이 비어 있으면 저장 없이 그 화면에서만 유지됩니다.
- 다른 사람이 쓴 내용은 5초 안에 반영됩니다.
