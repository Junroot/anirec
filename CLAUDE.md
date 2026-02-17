# CLAUDE.md

이 파일은 Claude Code(claude.ai/code)가 이 저장소의 코드를 다룰 때 참고하는 가이드입니다.

## 프로젝트 개요

AniRec은 애니메이션 인덱스 & AI 추천 플랫폼입니다. MAL(MyAnimeList) 보완 서비스로, 사용자는 애니메이션을 탐색하고(MAL 평점 표시), 개인 평점을 기록하며, MAL 사용자 클러스터링 + 개인 평점 기반의 AI 추천을 받습니다. 앱 내 상세 페이지는 없으며, 카드 클릭 시 MAL 페이지가 새 탭에서 열립니다.

상세 PRD: `docs/AniRec_PRD_v1.3.md`

## 저장소 구조

- `front/` — React SPA (현재 유일한 활성 코드베이스)
- `dist/` — 빌드 결과물 (루트에서 서빙, `front/`에서 빌드)
- `docs/` — 제품 문서

- `back/` — Spring Boot 3 + Kotlin 백엔드 API

추천 엔진(Python/scikit-learn)은 계획 단계이며 아직 구현되지 않았습니다.

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

**스타일링:** `front/src/index.css`에서 `@theme`(Tailwind v4 문법)으로 정의된 MD3(Material Design 3) 색상 토큰 기반 다크 테마. 주요 토큰: `primary`/`primary-container`(인디고 계열), `surface`/`surface-container`/`surface-container-high`/`surface-container-highest`(배경 계층), `on-surface`/`on-surface-variant`(텍스트), `outline`/`outline-variant`(보조 텍스트/테두리), `tertiary`(강조), `error`/`success`/`warning`/`info`(상태). 조건부 클래스에 `clsx` 사용.

**차트:** recharts 라이브러리로 통계 시각화 (장르 바 차트, 평점 히스토그램, 월별 히스토리, 탑 스튜디오).

**아이콘:** lucide-react

## 백엔드 명령어

모든 명령어는 `back/` 디렉토리에서 실행:

```bash
cd back
docker compose up -d    # MySQL 8 + Redis 7 로컬 인프라 기동
./gradlew build         # 컴파일 + 테스트
./gradlew bootRun       # 앱 실행 (8080 포트)
```

## 백엔드 아키텍처

**기술 스택:** Spring Boot 3 + Kotlin + WebFlux + Data JPA + Data Redis Reactive + Spring Security + OAuth2 Client

**빌드:** Gradle (Kotlin DSL) + JDK 21

**주요 의존성:** QueryDSL 5 (kapt), JJWT 0.12, kotlinx-coroutines, Testcontainers (MySQL), MockWebServer (테스트)

**`back/src/main/kotlin/com/anirec/` 패키지 구조:**
- `domain/auth/` — 인증 모듈
- `domain/anime/` — Jikan API 클라이언트(`client/`), DTO(`dto/`), 서비스(`service/`: `AnimeService` + `AnimeCacheService`), REST 컨트롤러(`controller/`)
- `domain/rating/` — 개인 평점
- `domain/recommendation/` — 추천 엔진 연동
- `global/config/` — 공통 설정
- `global/exception/` — 글로벌 예외 처리
- `global/security/` — Security 설정

**설정 프로필:** `dev` (로컬 MySQL/Redis), `prod` (환경변수 기반), `test` (H2 인메모리, Redis autoconfigure 제외)

**백엔드 코드 패턴:**
- 컨트롤러/서비스에서 Kotlin 코루틴 `suspend` 함수 사용, `awaitSingle()` 패턴으로 Mono → suspend 변환
- WebClient를 통한 외부 API 호출 (`JikanClient`)
- Redis 캐싱: cache-aside 패턴 (`AnimeCacheService`), 검색 TTL 1시간, 탑/시즌 TTL 6시간. Redis 장애 시 로그만 남기고 API 직접 호출 fallback
- 캐시 키: `anime:{operation}:{param=value}:...` 형식, null 파라미터 제외
- test 프로필에서 Redis가 제외되므로 Redis 의존 빈에 `@ConditionalOnBean(ReactiveRedisConnectionFactory::class)` 필수
- DTO의 snake_case 필드에 `@JsonProperty` 명시 (전역 네이밍 전략 대신 DTO별 지정)
- `AnimeService`는 `AnimeCacheService`를 `@Autowired(required = false)` nullable 주입. 있으면 캐시 위임, 없으면(test 프로필) `JikanClient` 직접 호출 fallback
- SecurityConfig에서 `GET /api/anime/**`는 `permitAll()`, 나머지 `/api/**`는 `authenticated()` (순서 중요)
- 테스트: MockWebServer로 WebClient 단위 테스트, Testcontainers(redis:7-alpine) + mockk로 캐싱 통합 테스트

## 현재 상태

프론트엔드는 목 데이터로 완전히 구성된 상태이며, 실제 API 연동은 아직 없습니다. 인증은 localStorage를 통한 가짜 구현입니다. 모든 애니메이션 데이터는 `data/mockAnime.ts`에서 제공됩니다. 백엔드는 인증 모듈(`domain/auth/`), Jikan API 클라이언트 + Redis 캐싱 레이어, 그리고 Anime REST API(`GET /api/anime`, `/api/anime/top`, `/api/anime/season`, `/api/anime/season/now`)가 구현된 상태입니다. `domain/rating/`, `domain/recommendation/`은 아직 미구현입니다. 다음 주요 단계는 프론트엔드-백엔드 연동입니다.
