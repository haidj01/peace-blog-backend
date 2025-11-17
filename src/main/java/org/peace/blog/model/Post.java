package org.peace.blog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Post 엔티티
 * 블로그 게시글을 나타내는 도메인 모델
 */
@Data  // Getter, Setter, toString, equals, hashCode 자동 생성
@Builder  // 빌더 패턴 지원
@NoArgsConstructor  // 기본 생성자
@AllArgsConstructor  // 모든 필드를 받는 생성자
public class Post {
    
    /**
     * 게시글 고유 ID
     */
    private Long id;
    
    /**
     * 게시글 제목
     */
    private String title;
    
    /**
     * 게시글 본문 (HTML 형식)
     */
    private String content;
    
    /**
     * 작성자명
     */
    private String author;
    
    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
}
