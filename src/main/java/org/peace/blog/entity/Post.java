package org.peace.blog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Post 엔티티
 * - DB에 저장되는 블로그 게시글 정보
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("posts")
public class Post {

    @Id
    private Long id;

    private String title;          // 게시글 제목
    private String content;        // 게시글 내용
    private String summary;        // 게시글 요약 (선택)
    private String username;       // 작성자 username

    private String status;         // 게시글 상태 (DRAFT, PUBLISHED, ARCHIVED)
    private String category;       // 카테고리
    private String[] tags;         // 태그 배열

    private Integer viewCount;     // 조회수
    private Boolean commentEnabled; // 댓글 허용 여부

    private LocalDateTime createdAt;  // 생성일시
    private LocalDateTime updatedAt;  // 수정일시
    private LocalDateTime publishedAt; // 발행일시
}
