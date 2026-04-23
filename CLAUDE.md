# CLAUDE.md

## 프로젝트 개요

DoDutch Backend - 여행 정산(더치페이) 애플리케이션 서버
여행 생성, 지출 기록, 멤버 간 비용 분할, 카카오페이 정산 기능 제공

## 기술 스택

- Java 17, Spring Boot 3.4.5, Gradle 9.1.0
- Spring Data JPA + MySQL
- Spring Security + JWT (jjwt 0.11.5)
- Spring WebFlux (외부 API 호출용)
- Swagger (springdoc-openapi 2.8.6)
- Lombok, AWS S3

## 프로젝트 구조

```
src/main/java/graduation/project/DoDutch_server/
├── domain/           # 기능 모듈
│   ├── auth/         # 인증 (카카오 OAuth)
│   ├── trip/         # 여행 관리
│   ├── expense/      # 지출 기록
│   ├── dutch/        # 더치페이 정산
│   ├── member/       # 멤버 관리
│   ├── photo/        # 사진 관리
│   ├── ocr/          # OCR (네이버 클로바)
│   └── kakaopay/     # 카카오페이 연동
└── global/
    ├── common/       # BaseEntity, ResponseDTO, 예외 처리
    ├── config/       # Security, Swagger, AWS, CORS 설정
    └── util/         # 유틸리티 클래스
```

각 도메인 모듈은 `controller/`, `service/`, `repository/`, `entity/`, `dto/` 패키지로 구성

## 코드 컨벤션

### 커밋 메시지
`[Type] 설명` 형식 사용
- Types: Feat, Fix, Docs, Style, Refactor, Chore, Rename, Remove, Environment, !HOTFIX

### 코드 패턴
- 모든 엔티티는 `BaseEntity` 상속 (createdAt, updatedAt 자동 관리)
- API 응답은 `ResponseDTO<T>` 래퍼 사용 (success, code, message, data)
- Lombok: `@Getter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- 엔티티: `@SuperBuilder(toBuilder = true)` 사용 (BaseEntity 상속 시)

## 리뷰 시 중점 사항

- 보안: JWT 처리, 인증/인가, 입력 검증
- JPA: N+1 쿼리, cascade/fetch 타입, orphanRemoval 설정
- API: ResponseDTO 패턴 준수, HTTP 상태 코드 적절성
- Null safety 및 validation 어노테이션 활용
