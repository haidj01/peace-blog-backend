-- admins 테이블 생성

CREATE TABLE user (
    id BIGSERIAL PRIMARY KEY,
    role VARCHAR(50) NOT NULL
    username VARCHAR(50) NOT NULL UNIQUE,
    passcode VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL
);

-- 초기 관리자 데이터 삽입 (예시)
-- 실제로는 passcode를 BCrypt 등으로 해시화해서 저장해야 합니다
INSERT INTO admins (username, passcode, email) 
VALUES ('admin', 'your-passcode', 'admin@peace.org');
