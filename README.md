# AITRICS BE Senior 채용 과제 - 환자 Vital Signs 모니터링 시스템

본 프로젝트는 Kotlin과 Spring Boot를 기반으로 구현된 병원 환자 생체징후 데이터 관리 및 위험도 추론 시스템입니다.

- [gemini.md](./gemini.md): AI 엔지니어링 파트너 Gemini와의 협업 과정 및 주요 결정 사항 기록
- [시스템_설계.md](./src/docs/시스템_설계.md): 시스템 아키텍처, 데이터베이스 설계 및 기술적 상세 구현 설명
- [테스트_절차서.md](./src/docs/테스트_절차서.md): Swagger UI를 통한 기능별 테스트 방법 및 시나리오 안내

## 🚀 빠른 시작 (Docker)

프로젝트 루트 폴더에서 아래 명령어를 실행하면 애플리케이션이 8080 포트에서 실행됩니다.

```bash
docker build -t aitrics-assignment .
docker run -p 8080:8080 aitrics-assignment
```

## 📖 API 문서 (Swagger)

애플리케이션 실행 후 아래 URL을 통해 상세한 API 명세를 확인할 수 있습니다.
- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## 🔑 인증 방법 (JWT)

모든 API는 Bearer Token 인증이 필요합니다. 테스트를 위해 아래 절차를 따라주세요.

1. **토큰 발급**: `GET /api/v1/auth/token?username=reviewer` 호출
2. **헤더 적용**: 발급받은 토큰을 `Authorization: Bearer <token>` 헤더에 포함하여 API 호출

## 🛠 주요 설계 및 구현 특징

### 1. 낙관적 락 (Optimistic Lock)
- **환자 수정 API** 및 **Vital UPSERT API**에 적용되었습니다.
- JPA의 `@Version`을 사용하며, 충돌 시 **409 Conflict**와 함께 RFC 7807 표준 에러 응답을 반환합니다.
- 서비스 레이어에서 명시적으로 `version` 대조 로직을 포함하여 비즈니스 일관성을 보장합니다.

### 2. Vital 데이터 UPSERT
- `(patient_id, recorded_at, vital_type)` 복합 식별자를 사용하여 데이터의 유일성을 보장합니다.
- 기존 데이터 존재 여부에 따라 INSERT(version 1 시작) 또는 UPDATE(낙관적 락 적용)가 자동으로 수행됩니다.

### 3. Inference 엔진 (Strategy Pattern)
- 위험도 판정 규칙을 `InferenceRule` 인터페이스로 추상화하여 확장성을 확보했습니다.
- 현재 HR, SBP, SpO2에 대한 3가지 규칙이 적용되어 있으며, 환경변수(`VITAL_RISK_TIME_WINDOW_HOURS`)로 설정된 기간의 데이터를 분석합니다.

### 4. 기술 스택
- **언어**: Kotlin 1.9
- **프레임워크**: Spring Boot 3.2
- **DB**: SQLite (database.db 파일 생성)
- **보안**: Spring Security + JWT

## 📁 프로젝트 구조
- `common/`: 전역 예외 처리 및 공통 유틸리티
- `auth/`: JWT 인증 및 Security 설정
- `domain/`: 엔티티 및 핵심 도메인 모델
- `service/`: 비즈니스 로직 및 Inference 전략
- `controller/`: REST API 엔드포인트 및 DTO
