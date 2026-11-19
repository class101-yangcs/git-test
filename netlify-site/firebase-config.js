// Firebase 콘솔 → 프로젝트 설정 → 내 앱(웹)에서 복사한 firebaseConfig 값을 아래에 붙여 넣으세요.
// 이 값들은 브라우저에 공개되는 식별자라 비밀번호가 아니에요. 대신 보안은 firestore.rules 로 지켜요.
// null 로 두면 Firebase 없이 각 브라우저에만 저장돼요.
window.FIREBASE_CONFIG = null;
/* 예시
window.FIREBASE_CONFIG = {
  apiKey: "AIza...",
  authDomain: "your-project.firebaseapp.com",
  projectId: "your-project",
  storageBucket: "your-project.appspot.com",
  messagingSenderId: "1234567890",
  appId: "1:1234567890:web:abcdef",
  buildingId: "yeonmaeul"   // 같은 프로젝트에 건물이 여러 개면 이름만 바꿔 구분
};
*/
