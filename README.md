# OTA Service

Spring Boot 기반 OTA 서비스 프로젝트입니다.
- 프로젝트 전반에 걸쳐서 Codex를 활용한 Vibe Coding을 진행했습니다.
  - 설계 문서 초안을 작성한 뒤, Codex를 활용해 내용을 보완했습니다.
  - 각 API의 Request, Response, 검증 규칙, 상태 전이 기준은 직접 정의해 전달했으며, Codex는 이를 바탕으로 코드 구현과 세부 보완 작업을 진행했습니다.

## 문서 안내
- 진행 기록: [docs/progress](./docs/progress/)
- 도메인 모델 설계 문서: [docs/DomainDesign.md](./docs/DomainDesign.md)
- DB 설계 문서: [docs/DatabaseDesign.md](./docs/DatabaseDesign.md)
- 설계 관련 도식화: [docs/image](./docs/image/)

## 실행 전 준비
- Java 21
- MySQL 8.0 이상
- Gradle Groovy DSL

MySQL에서 먼저 DB를 생성합니다.

```sql
CREATE DATABASE ota_service;
```

기본 DB 연결 정보는 [application.yml](./src/main/resources/application.yml)에 있습니다.
- url: `jdbc:mysql://localhost:3306/ota_service`
- username, password 계정 정보는 사용자 로컬 환경에 맞게 수정해야 합니다.

애플리케이션 시작 시 `schema.sql`, `data.sql`이 자동 실행됩니다.

## 프로젝트 실행

```bash
./gradlew bootRun
```

## Swagger
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- Extranet API와 Customer API가 Swagger group으로 분리되어 있습니다.
