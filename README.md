# Gaayong

실제로 내가 쓸 수 있는 가용금액이 얼마인지 볼 수 있는 개인 가계부입니다.

가용금액은 통장 잔고에서 아직 빠져나갈 고정지출과 카드대금을 뺀, 지금 쓸 수 있는 돈입니다.

> **상태:** Thymeleaf 서버 템플릿 UI에서 Vue SPA로 마이그레이션 중입니다. 현재 진입 경로는 Vue SPA + `/api` JSON입니다.

## 가용금액

대시보드(`/api/home`) 계산:

```
가용금액 = 통장 잔고 − (미결제 고정지출 + 미결제 카드대금)
예상 가용금액 = 예산 − 고정지출
차이 = 가용금액 − 예상 가용금액
```

지출·수입·고정지출·예산·통장·카드·카테고리로 이 숫자를 맞춥니다.

## 빠른 시작

필요 환경:

- JDK 17 (`pom.xml`)
- Node.js 22 (`.nvmrc`, 빌드 시 `v22.22.0`)
- MariaDB

설정 파일(`application.yml`, `application-*.yml`)은 `.gitignore`의 `*.yml` 때문에 저장소에 없습니다. 로컬에 Spring 프로필 설정을 두고 데이터소스·세션 JDBC를 채워야 합니다.

<!-- TODO: application 예시(비밀값 없는 템플릿)를 저장소에 둘지 결정 -->

### 프론트만

```bash
cd frontend
npm ci
npm run dev
```

Vite 개발 서버는 `http://localhost:5174` (`frontend/vite.config.ts`). `fetch`는 상대 경로 `/api`를 쓰므로, API는 별도 Spring 프로세스가 같은 출처에서 응답해야 합니다. Vite proxy 설정은 없습니다.

### 서버(SPA 포함)

```bash
./mvnw spring-boot:run
```

Windows는 `mvnw.cmd spring-boot:run`입니다. `generate-resources`에서 `frontend`의 `npm ci` / `npm run build`가 돌아가고, 빌드 결과가 classpath `static`으로 들어갑니다.

로컬 개발 프로필의 서버 포트는 `9090`입니다(로컬 `application-dev.yml` 기준).

## 구성

| 영역 | 기술 |
| --- | --- |
| API | Spring Boot 3.4.4, Java 17, Security, MyBatis, Spring Session JDBC (`pom.xml`) |
| DB | MariaDB (`mariadb-java-client`) |
| 화면 | Vue 3, TypeScript, Vite, Pinia, Vue Router (`frontend/package.json`) |
| 패키징 | WAR, `finalName`=`ROOT`. `frontend/dist` → `static` (`pom.xml`) |

라우트는 `SpaController`가 `forward:/index.html`로 넘깁니다. JSON은 `com.gaayong.api.*`의 `/api/**`입니다. 로그인 세션은 쿠키 + JDBC 세션입니다.

## 화면

`frontend/src/router/index.ts` 기준:

- `/` 대시보드
- `/expense` 지출, `/pay` 지출현황
- `/income` 수입
- `/fixed` 고정지출
- `/budget` 예산
- `/account` 통장, `/card` 카드, `/category` 카테고리
- `/signin`, `/signup`

인증이 필요한 경로는 로그인 후에만 들어갑니다.

## 빌드 · 배포

```bash
./mvnw package
```

산출물: `target/ROOT.war`

`main`에 `src/**`, `frontend/**`, `pom.xml`, `.nvmrc`가 바뀌면 `.github/workflows/deploy.yml`이 서버의 `deploy.sh`를 SSH로 실행합니다.
