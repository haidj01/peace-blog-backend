package org.peace.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 게시글 수정 요청 DTO
 * 클라이언트로부터 받는 게시글 수정 데이터
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {

    /**
     * 게시글 제목 (필수, 1-255자)
     */
    @NotBlank(message = "제목은 필수입니다")
    @Size(min = 1, max = 255, message = "제목은 1-255자 사이여야 합니다")
    private String title;

    /**
     * 게시글 본문 (필수)
     */
    @NotBlank(message = "내용은 필수입니다")
    private String content;

    /**
     * 게시글 요약 (선택)
     */
    private String summary;

    /**
     * 카테고리 (선택)
     */
    private String category;

    /**
     * 태그 배열 (선택)
     */
    private String[] tags;

    /**
     * 댓글 허용 여부 (선택)
     */
    private Boolean commentEnabled;
}
