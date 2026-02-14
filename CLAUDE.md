# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소의 코드를 다룰 때 참고하는 가이드입니다.

## 프로젝트 개요

AniRec은 애니메이션 인덱스 & AI 추천 플랫폼입니다. MAL(MyAnimeList) 보완 서비스로, 사용자는 애니메이션을 탐색하고(MAL 평점 표시), 개인 평점을 기록하며, MAL 사용자 클러스터링 + 개인 평점 기반의 AI 추천을 받습니다. 앱 내 상세 페이지는 없으며, 카드 클릭 시 MAL 페이지가 새 탭에서 열립니다.

상세 PRD: `docs/AniRec_PRD_v1.3.md`

## 저장소 구조

- `front/` — React SPA (현재 유일한 활성 코드베이스)
- `dist/` — 빌드 결과물 (루트에서 서빙, `front/`에서 빌드)
- `docs/` — 제품 문서

백엔드(Spring Boot + Kotlin)와 추천 엔진(Python/scikit-learn)은 계획 단계이며 아직 구현되지 않았습니다.

## 프론트엔드 명령어

모든 명령어는 `front/` 디렉토리에서 실행:

```bash
cd front
npm run dev       # Vite 개발 서버 (HMR)
npm run build     # TypeScript 검사 + Vite 프로덕션 빌드
npm run lint      # ESLint
npm run preview   # 프로덕션 빌드 미리보기
```

## 프론트엔드 아키텍처

**기술 스택:** React 19 + TypeScript + Tailwind CSS v4 + Vite 7 + react-router v7

**경로 별칭:** `@/`는 `front/src/`에 매핑 (`vite.config.ts`와 `tsconfig.app.json` 양쪽에 설정)

**`front/src/` 주요 디렉토리:**
- `pages/` — 라우트 단위 페이지 컴포넌트
- `components/` — 도메인별 구성: `ui/`, `layout/`, `anime/`, `auth/`, `filters/`, `home/`, `rating/`, `recommendation/`, `stats/`
- `types/` — Jikan API 형태에 맞춘 TypeScript 인터페이스 (예: `Anime` 타입은 `mal_id`, `images.jpg.large_image_url` 사용)
- `data/` — 목 데이터 파일 및 상수 (실제 API 호출 없음)
- `context/` — React 컨텍스트 (localStorage 기반 목 인증의 AuthContext)
- `hooks/` — 커스텀 훅 (`useAuth`, `useLocalStorage`, `useFilterState`)

**라우팅 (`router.tsx`):**
- `/` — 홈 (공개)
- `/search/anime` — 탐색/검색 (공개)
- `/login`, `/signup` — 인증 페이지 (AuthLayout)
- `/recommend`, `/my-ratings`, `/stats` — 보호된 라우트 (인증 필요)

**레이아웃:**
- `MainLayout` — Navbar + 콘텐츠 + Footer (대부분의 페이지)
- `AuthLayout` — 중앙 정렬 카드 레이아웃 (로그인/회원가입)
- `ProtectedRoute` — 미인증 사용자 리다이렉트

**스타일링:** `front/src/index.css`에서 `@theme`(Tailwind v4 문법)으로 정의된 커스텀 색상 토큰의 다크 테마. 주요 색상: `primary` (#6366f1 인디고), `surface` (#1e1b2e), `background` (#0f0d1a). 조건부 클래스에 `clsx` 사용.

**차트:** recharts 라이브러리로 통계 시각화 (장르 바 차트, 평점 히스토그램, 월별 히스토리, 탑 스튜디오).

**아이콘:** lucide-react

## 현재 상태

프론트엔드는 목 데이터로 완전히 구성된 상태이며, 실제 API 연동은 아직 없습니다. 인증은 localStorage를 통한 가짜 구현입니다. 모든 애니메이션 데이터는 `data/mockAnime.ts`에서 제공됩니다. 다음 주요 단계는 Jikan API(MAL) 연동과 Spring Boot 백엔드 구축입니다.
