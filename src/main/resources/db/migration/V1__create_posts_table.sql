-- Posts 테이블 생성
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    summary TEXT,
    username VARCHAR(255) NOT NULL,

    status VARCHAR(20) DEFAULT 'DRAFT',
    category VARCHAR(100),
    tags TEXT[],

    view_count INTEGER DEFAULT 0,
    comment_enabled BOOLEAN DEFAULT true,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP,

    CONSTRAINT fk_username FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX idx_posts_username ON posts(username);
CREATE INDEX idx_posts_status ON posts(status);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_posts_category ON posts(category);
CREATE INDEX idx_posts_published_at ON posts(published_at DESC);

-- 코멘트 추가
COMMENT ON TABLE posts IS '블로그 게시글 테이블';
COMMENT ON COLUMN posts.id IS '게시글 고유 ID';
COMMENT ON COLUMN posts.title IS '게시글 제목';
COMMENT ON COLUMN posts.content IS '게시글 본문 (HTML/Markdown)';
COMMENT ON COLUMN posts.summary IS '게시글 요약';
COMMENT ON COLUMN posts.username IS '작성자 username (users 테이블 참조)';
COMMENT ON COLUMN posts.status IS '게시글 상태 (DRAFT, PUBLISHED, ARCHIVED)';
COMMENT ON COLUMN posts.category IS '게시글 카테고리';
COMMENT ON COLUMN posts.tags IS '게시글 태그 배열';
COMMENT ON COLUMN posts.view_count IS '조회수';
COMMENT ON COLUMN posts.comment_enabled IS '댓글 허용 여부';
COMMENT ON COLUMN posts.created_at IS '생성 일시';
COMMENT ON COLUMN posts.updated_at IS '수정 일시';
COMMENT ON COLUMN posts.published_at IS '발행 일시';
