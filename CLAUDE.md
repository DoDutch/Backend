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

## 테스트 작성 규칙

### 테스트 스택
- JUnit 5 + Mockito + AssertJ + Spring Boot Test

### 파일 위치 규칙
- 단위 테스트: `*Test.java`, 통합 테스트: `*IT.java`
- 패키지 구조는 `src/main/java`와 미러링
- 소스 패키지명 그대로 사용: `graduation.project.DoDutch_server.*`

### 네이밍
- `@DisplayName`에 한글 사용 (예: `"여행 생성 시 시작일이 종료일 이후면 예외 발생"`)
- 메서드명: `should_기대동작_when_조건` 패턴

### 구조
- Given-When-Then (Arrange-Act-Assert)
- `@BeforeEach`로 공통 셋업, 각 테스트 독립성 확보

### Spring 테스트 슬라이스 기준

| 대상 | 어노테이션 | 비고 |
|------|-----------|------|
| Controller | `@WebMvcTest` | MockMvc 사용, Security 설정 주의 |
| Repository | `@DataJpaTest` | H2 인메모리 DB |
| Service | `@ExtendWith(MockitoExtension.class)` | Spring 컨텍스트 로드 금지 |
| 통합 테스트 | `@SpringBootTest` | 최소한으로 사용 |

### 프로파일 규칙
- 모든 `@SpringBootTest`, `@DataJpaTest`, `@WebMvcTest` 클래스는 반드시 `@ActiveProfiles("test")` 추가
- `application-test.yml`이 활성화되지 않으면 환경변수 누락으로 컨텍스트 로딩 실패

### Mocking 규칙
- BDDMockito 스타일: `given(...).willReturn(...)`
- 외부 의존성만 목킹 (Repository, 외부 API 클라이언트)
- 도메인 객체(Entity, DTO)는 실제 인스턴스 사용
- 외부 API(카카오, 네이버 CLOVA, OpenAI, AWS S3, KakaoPay)는 반드시 목킹

### 금지 사항
- `@Autowired` 필드 주입 (→ 생성자 주입 또는 `@Mock`/`@InjectMocks`)
- `Thread.sleep()` (→ 비동기 대기 필요 시 Awaitility 도입)
- 테스트 통과시키려고 프로덕션 코드 수정
- 허술한 assertion (`isNotNull`만 사용 등)
- 테스트에서 `@Transactional` 사용 (테스트 격리 오염)

### 커버리지 목표
- Line 80%, Branch 70%
