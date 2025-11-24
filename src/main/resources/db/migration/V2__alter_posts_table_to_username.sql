-- 기존 posts 테이블이 있는 경우 author_id를 username으로 변경하는 마이그레이션

-- 1. 기존 외래키 제약조건 삭제
ALTER TABLE posts DROP CONSTRAINT IF EXISTS fk_author;

-- 2. username 컬럼 추가 (임시로 NULL 허용)
ALTER TABLE posts ADD COLUMN IF NOT EXISTS username VARCHAR(255);

-- 3. 기존 데이터가 있다면 author_id를 기반으로 username 채우기
-- (author_id가 users 테이블의 id를 참조하는 경우)
UPDATE posts p
SET username = (SELECT u.username FROM users u WHERE u.id = p.author_id)
WHERE p.author_id IS NOT NULL AND p.username IS NULL;

-- 4. username을 NOT NULL로 변경
ALTER TABLE posts ALTER COLUMN username SET NOT NULL;

-- 5. author_id 컬럼 삭제
ALTER TABLE posts DROP COLUMN IF EXISTS author_id;

-- 6. 새로운 외래키 제약조건 추가
ALTER TABLE posts ADD CONSTRAINT fk_username
    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE;

-- 7. 인덱스 재생성
DROP INDEX IF EXISTS idx_posts_author_id;
CREATE INDEX IF NOT EXISTS idx_posts_username ON posts(username);

-- 8. 코멘트 업데이트
COMMENT ON COLUMN posts.username IS '작성자 username (users 테이블 참조)';
