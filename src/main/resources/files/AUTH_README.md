# Peace Blog - Admin 로그인 기능

## 📁 생성된 파일

### Entity & Repository
- `entity/Admin.java` - Admin 엔티티
- `repository/AdminRepository.java` - Admin R2DBC Repository

### Service
- `service/AuthService.java` - 인증 서비스 (핵심 로직)
- `service/EmailService.java` - 이메일 발송 서비스

### Util & Controller
- `util/JwtUtil.java` - JWT 생성/검증 유틸리티
- `controller/AuthController.java` - 인증 API 컨트롤러

## 🔧 설정

### 1. build.gradle 의존성 추가
```gradle
dependencies {
    implementation 'io.jsonwebtoken:jjwt-api:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.3'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.3'
    implementation 'org.springframework.boot:spring-boot-starter-mail'
    implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
    runtimeOnly 'org.postgresql:r2dbc-postgresql'
}
```

### 2. application.yml 설정
`application.yml.example` 파일 참고하여 설정

### 3. DB 테이블 생성
`schema.sql` 실행

## 🚀 API 사용법

### 1️⃣ 인증번호 요청
```http
POST /api/admin/auth/request
Content-Type: application/json

{
  "username": "admin",
  "passcode": "your-passcode"
}
```

**응답:**
```json
{
  "success": true,
  "message": "인증번호가 이메일로 발송되었습니다",
  "data": null
}
```

### 2️⃣ 인증번호 검증 및 JWT 발급
```http
POST /api/admin/auth/verify
Content-Type: application/json

{
  "username": "admin",
  "code": "123456"
}
```

**응답:**
```json
{
  "success": true,
  "message": "인증 성공",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3️⃣ JWT 토큰 검증
```http
GET /api/admin/verify-token
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📋 인증 플로우

1. Admin이 username + passcode 제공
2. 시스템이 DB에서 검증
3. 검증 성공 시 6자리 인증번호 생성
4. 등록된 이메일로 인증번호 발송
5. Admin이 인증번호 입력
6. 검증 성공 시 3시간 유효한 JWT 발급
7. 이후 요청에 JWT 사용

## ⚠️ 보안 개선 사항

1. **패스코드 해시화**: BCrypt 사용 권장
2. **인증번호 저장**: 프로덕션에서는 Redis 사용
3. **JWT Secret**: 256bit 이상 강력한 키 사용
4. **Rate Limiting**: 인증 시도 횟수 제한
5. **HTTPS**: 프로덕션에서 필수

## 📝 다음 단계

1. 의존성 추가
2. 설정 파일 수정
3. DB 테이블 생성
4. 이메일 계정 설정 (Gmail 앱 비밀번호)
5. 테스트
