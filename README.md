> 백엔드 개발자가 구현한 포트폴리오 자문 서버 - JWT 인증, Redis 캐시, Docker 기반 운영

# 💼 포트폴리오 자문 백엔드 API

포트폴리오 자문 서비스를 위한 백엔드 API입니다.  
JWT 기반 인증 시스템, 사용자 잔고 관리, 증권 등록 및 포트폴리오 자문 기능을 구현했습니다.

---

## 📌 프로젝트 개요

- **목표**: 사용자 맞춤형 포트폴리오 자문 API 제공
- **핵심 기능**: 로그인/회원가입, JWT 인증, 잔고 입출금, 자문 요청, 증권 관리
- **구현 방식**: Java 21 + Spring Boot 3 기반 REST API
- **보안**: JWT 기반 인증 + Redis 블랙리스트로 로그아웃 처리

---

## 🛠 기술 스택

| 영역           | 사용 기술                         |
|----------------|----------------------------------|
| Language       | Java 21                          |
| Framework      | Spring Boot 3.4.5                |
| ORM            | Spring Data JPA (Hibernate)      |
| Database       | H2 (개발), MySQL (Docker 환경)    |
| Auth           | Spring Security + JWT            |
| Cache/Token    | Redis (블랙리스트 저장)          |
| Build Tool     | Gradle                           |
| Container      | Docker, Docker Compose           |
| Test           | JUnit5, MockMvc                  |

---

## 📂 주요 기능

### ✅ 인증 / 인가
- 회원가입 (userId, password 암호화)
- 로그인 (JWT 발급)
- 로그아웃 (Redis 블랙리스트 등록)
- 토큰 유효성 검증 + `@AuthenticationPrincipal`로 사용자 식별
- 회원가입, 로그인 외 모든 기능 인증 필요

### ✅ 사용자 기능
- **잔고 입금 / 출금 / 조회**
- **자문 요청**: 위험도 기반 포트폴리오 구성
    - HIGH: 전체 잔고 사용
    - MEDIUM: 절반 투자
    - 증권 최적화: 그리디 알고리즘 기반 분산 투자

### ✅ 관리자 기능
- 증권 등록 / 삭제 / 가격 수정
- 사용자 역할 변경 (USER → ADMIN)
- 기본 admin 계정:
    - ID: `admin`
    - PW: `admin1234`

---

## 🧪 테스트

- 모든 핵심 기능에 대한 테스트 작성
- `@WebMvcTest`, `@SpringBootTest`, `@MockitoBean` 활용
- 테스트 커버:
    - 로그인/로그아웃
    - 입금/출금/조회
    - 자문 요청
    - 포트폴리오 구성
    - 역할 변경
    - 증권 등록/수정/삭제
- `ControllerTestSupport`, `IntegrationTest`로 Spring Boot 구동 최소화 및 공통 환경 설정
```bash
./gradlew test
```

---

## 🐳 실행 방법
```bash
./gradlew clean build
docker compose up --build
```
`docker compose up --build` 명령어 실패 시 `docker-compose up --build`

- API 서버: http://localhost:8080

---

## ✅ API 테스트 (Postman)

- [Postman Collection](./postman/aim-advice-server.postman_collection.json)
- 테스트 순서:
    1. 회원가입
    2. 로그인 요청으로 토큰 발급
    3. 이후 요청에 Header Authorization `Bearer Token`으로 설정

---

## 📘 Swagger 문서

- 로컬 실행 후 Swagger UI에서 전체 API 확인 가능:
    - http://localhost:8080/swagger-ui/index.html
- Authorize 버튼 클릭 후 JWT 토큰 입력 시 테스트 가능

---
## 🗃 DB 정보

- JDBC URL: `jdbc:mysql://mysql:3306/advice`
- DB 이름: `advice`
- 사용자: `advice_user`
- 비밀번호: `advice_pass`

> 로컬 클라이언트 (ex: DBeaver)로 접속 시:
> - Host: `localhost`
> - Port: `3306`
> - ID/PW는 동일
---
## 🙋‍♂️ 개발자 소개

- 이름: 박찬건 (ChanGeon Park)
- 역할: 백엔드 개발 (Spring Boot 기반 API 서버 구현)
- 이메일: jg9870@naver.com

> 고객에게 최고의 가치를 제공하는 것을 목표로 합니다.