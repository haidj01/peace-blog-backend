package org.peace.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 생성 요청 DTO
 * 클라이언트로부터 받는 게시글 작성 데이터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {
    
    /**
     * 게시글 제목 (필수, 1-200자)
     */
    @NotBlank(message = "제목은 필수입니다")
    @Size(min = 1, max = 200, message = "제목은 1-200자 사이여야 합니다")
    private String title;
    
    /**
     * 게시글 본문 (필수)
     */
    @NotBlank(message = "내용은 필수입니다")
    private String content;
    
    /**
     * 작성자명 (필수, 1-50자)
     */
    @NotBlank(message = "작성자명은 필수입니다")
    @Size(min = 1, max = 50, message = "작성자명은 1-50자 사이여야 합니다")
    private String author;
}
