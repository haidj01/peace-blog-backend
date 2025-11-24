# Peace Blog Backend

Spring WebFlux 기반 리액티브 블로그 백엔드 API

## 🚀 기술 스택

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring WebFlux** - 리액티브 웹 프레임워크
- **Lombok** - 보일러플레이트 코드 제거
- **Gradle** - 빌드 도구

## 📁 프로젝트 구조

```
src/main/java/org/peace/blog/
├── PeaceBlogApplication.java     # 애플리케이션 진입점
├── config/
│   └── WebConfig.java            # CORS 설정
├── controller/
│   ├── PostController.java       # 게시글 REST API
│   └── ImageController.java      # 이미지 업로드 API
├── service/
│   ├── PostService.java          # 게시글 비즈니스 로직
│   └── ImageService.java         # 이미지 업로드 로직 (Mock)
├── repository/
│   └── PostRepository.java       # In-Memory 저장소 (Mock)
└── model/
    ├── Post.java                 # 게시글 엔티티
    └── PostCreateRequest.java    # 게시글 작성 DTO
```

## 🔧 현재 상태 (Mock 데이터)

**현재 버전은 Mock 데이터를 사용합니다:**
- 데이터베이스 연결 없음
- In-Memory ConcurrentHashMap으로 데이터 저장
- 애플리케이션 재시작 시 데이터 초기화
- 초기 샘플 게시글 3개 자동 생성

## 📡 API 엔드포인트

### 게시글 API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/peace-blog/posts` | 모든 게시글 조회 |
| GET | `/peace-blog/posts/{id}` | 특정 게시글 조회 |
| POST | `/peace-blog/posts` | 게시글 작성 |
| PUT | `/peace-blog/posts/{id}` | 게시글 수정 |
| DELETE | `/peace-blog/posts/{id}` | 게시글 삭제 |
| GET | `/peace-blog/posts/count` | 게시글 개수 조회 |

### 이미지 API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/peace-blog/images/upload` | 이미지 업로드 (Mock) |
| POST | `/peace-blog/images/upload/base64` | Base64 이미지 업로드 (Mock) |

## 🏃 실행 방법

### 1. 필수 요구사항

- Java 17 이상
- Gradle (프로젝트에 포함된 Gradle Wrapper 사용 가능)

### 2. 애플리케이션 실행

**Gradle Wrapper 사용 (권장):**
```bash
# Unix/Mac
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

**또는 Gradle이 설치되어 있다면:**
```bash
gradle bootRun
```

**또는 JAR 빌드 후 실행:**
```bash
./gradlew build
java -jar build/libs/peace-blog-backend-0.0.1-SNAPSHOT.jar
```

### 3. 서버 확인

```bash
http://localhost:8080/peace-blog/posts
```

성공 시 초기 Mock 게시글 3개가 JSON으로 반환됩니다.

## 📝 API 사용 예시

### 1. 모든 게시글 조회
```bash
curl http://localhost:8080/peace-blog/posts
```

### 2. 특정 게시글 조회
```bash
curl http://localhost:8080/peace-blog/posts/1
```

### 3. 게시글 작성
```bash
curl -X POST http://localhost:8080/peace-blog/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "새 게시글",
    "content": "<p>게시글 내용입니다.</p>",
    "author": "홍길동"
  }'
```

### 4. 게시글 수정
```bash
curl -X PUT http://localhost:8080/peace-blog/posts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "수정된 제목",
    "content": "<p>수정된 내용</p>",
    "author": "홍길동"
  }'
```

### 5. 게시글 삭제
```bash
curl -X DELETE http://localhost:8080/peace-blog/posts/1
```

### 6. 이미지 업로드 (Mock)
```bash
curl -X POST http://localhost:8080/peace-blog/images/upload \
  -F "file=@/path/to/image.jpg"
```

## 🔄 리액티브 프로그래밍 (WebFlux)

이 프로젝트는 Spring WebFlux를 사용하여 리액티브 방식으로 구현되었습니다.

**주요 특징:**
- **Non-blocking I/O**: 스레드 블로킹 없이 비동기 처리
- **Backpressure**: 데이터 흐름 제어
- **높은 처리량**: 적은 스레드로 많은 요청 처리

**리액티브 타입:**
- `Mono<T>`: 0-1개의 데이터를 비동기로 처리
- `Flux<T>`: 0-N개의 데이터 스트림을 비동기로 처리

```java
// 예시: 단일 게시글 조회
public Mono<Post> getPostById(Long id) {
    return postRepository.findById(id);
}

// 예시: 모든 게시글 조회
public Flux<Post> getAllPosts() {
    return postRepository.findAll();
}
```

## 🌐 CORS 설정

프론트엔드와의 연동을 위해 CORS가 설정되어 있습니다.

**허용된 출처:**
- `http://localhost:3000` (React 개발 서버)
- `http://localhost:3001`
- `https://peace.org`

**application.yml에서 수정 가능:**
```yaml
cors:
  allowed-origins:
    - http://localhost:3000
    - https://your-domain.com
```

## 🔜 다음 단계 (실제 DB 연동)

Mock 데이터를 실제 데이터베이스로 전환하려면:

1. **PostgreSQL 의존성 추가** (build.gradle)
```gradle
implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
implementation 'org.postgresql:r2dbc-postgresql'
```

2. **application.yml에 DB 설정 추가**
```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/peace_blog
    username: postgres
    password: your_password
```

3. **Repository를 R2DBC Repository로 변경**
```java
public interface PostRepository extends ReactiveCrudRepository<Post, Long> {
}
```

4. **Entity에 @Table, @Id 어노테이션 추가**

## 🧪 테스트

```bash
./gradlew test
```

## 📦 빌드

```bash
./gradlew build
```

빌드 결과물: `build/libs/peace-blog-backend-0.0.1-SNAPSHOT.jar`

## 🐳 Docker (선택사항)

```dockerfile
FROM openjdk:17-jdk-slim
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 📖 참고 자료

- [Spring WebFlux 공식 문서](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Project Reactor](https://projectreactor.io/docs/core/release/reference/)
- [Spring Boot 공식 가이드](https://spring.io/guides)

## 🛠 개발 환경 설정

### IntelliJ IDEA
1. File > Open > build.gradle 선택
2. Gradle 자동 임포트 허용
3. Lombok 플러그인 설치
4. Enable annotation processing 활성화

### VS Code
1. Extension Pack for Java 설치
2. Spring Boot Extension Pack 설치
3. 프로젝트 폴더 열기

## 📄 라이선스

MIT License
- DJ -
