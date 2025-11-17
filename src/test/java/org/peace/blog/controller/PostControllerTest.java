package org.peace.blog.controller;

import org.junit.jupiter.api.Test;
import org.peace.blog.model.Post;
import org.peace.blog.model.PostCreateRequest;
import org.peace.blog.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * PostController 테스트
 * WebFlux 컨트롤러 단위 테스트
 */
@WebFluxTest(PostController.class)
class PostControllerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @MockBean
    private PostService postService;
    
    @Test
    void getAllPosts_ShouldReturnAllPosts() {
        // Given: Mock 데이터 준비
        Post post1 = createMockPost(1L, "테스트 제목 1");
        Post post2 = createMockPost(2L, "테스트 제목 2");
        
        when(postService.getAllPosts())
                .thenReturn(Flux.just(post1, post2));
        
        // When & Then: API 호출 및 검증
        webTestClient.get()
                .uri("/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Post.class)
                .hasSize(2);
    }
    
    @Test
    void getPostById_WhenPostExists_ShouldReturnPost() {
        // Given
        Post post = createMockPost(1L, "테스트 제목");
        
        when(postService.getPostById(1L))
                .thenReturn(Mono.just(post));
        
        // When & Then
        webTestClient.get()
                .uri("/posts/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Post.class)
                .isEqualTo(post);
    }
    
    @Test
    void getPostById_WhenPostNotExists_ShouldReturn404() {
        // Given
        when(postService.getPostById(999L))
                .thenReturn(Mono.empty());
        
        // When & Then
        webTestClient.get()
                .uri("/posts/999")
                .exchange()
                .expectStatus().isNotFound();
    }
    
    @Test
    void createPost_WithValidRequest_ShouldReturnCreatedPost() {
        // Given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("새 게시글")
                .content("게시글 내용")
                .author("작성자")
                .build();
        
        Post createdPost = createMockPost(1L, "새 게시글");
        
        when(postService.createPost(any(PostCreateRequest.class)))
                .thenReturn(Mono.just(createdPost));
        
        // When & Then
        webTestClient.post()
                .uri("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Post.class)
                .isEqualTo(createdPost);
    }
    
    @Test
    void deletePost_WhenPostExists_ShouldReturn204() {
        // Given
        when(postService.deletePost(1L))
                .thenReturn(Mono.empty());
        
        // When & Then
        webTestClient.delete()
                .uri("/posts/1")
                .exchange()
                .expectStatus().isNoContent();
    }
    
    /**
     * Mock Post 객체 생성 헬퍼 메서드
     */
    private Post createMockPost(Long id, String title) {
        return Post.builder()
                .id(id)
                .title(title)
                .content("테스트 내용")
                .author("테스트 작성자")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
