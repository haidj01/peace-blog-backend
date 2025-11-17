package org.peace.blog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.peace.blog.model.Post;
import org.peace.blog.model.PostCreateRequest;
import org.peace.blog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * PostController
 * 게시글 관련 REST API 엔드포인트
 * 
 * 모든 엔드포인트는 /api 접두사를 가짐 (application.yml 설정)
 */
@Slf4j
@RestController
@RequestMapping("/posts")  // 실제 경로: /api/posts
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    /**
     * 모든 게시글 조회
     * GET /api/posts
     * 
     * @return Flux<Post> - 모든 게시글 리스트
     */
    @GetMapping
    public Flux<Post> getAllPosts() {
        log.info("GET /api/posts - Getting all posts");
        return postService.getAllPosts();
    }
    
    /**
     * 특정 게시글 조회
     * GET /api/posts/{id}
     * 
     * @param id 게시글 ID
     * @return Mono<ResponseEntity<Post>> - 조회된 게시글 또는 404
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Post>> getPostById(@PathVariable Long id) {
        log.info("GET /api/posts/{} - Getting post by id", id);
        
        return postService.getPostById(id)
                // 게시글이 있으면 200 OK와 함께 반환
                .map(ResponseEntity::ok)
                // 게시글이 없으면 404 Not Found 반환
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    /**
     * 새 게시글 작성
     * POST /api/posts
     * 
     * @param request 게시글 작성 요청 (제목, 내용, 작성자)
     * @return Mono<ResponseEntity<Post>> - 생성된 게시글과 201 Created
     */
    @PostMapping
    public Mono<ResponseEntity<Post>> createPost(
            @Valid @RequestBody PostCreateRequest request) {
        
        log.info("POST /api/posts - Creating new post: {}", request.getTitle());
        
        return postService.createPost(request)
                // 201 Created 상태 코드와 함께 생성된 게시글 반환
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                // 에러 발생 시 500 Internal Server Error
                .onErrorResume(error -> {
                    log.error("Error creating post", error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
    
    /**
     * 게시글 수정
     * PUT /api/posts/{id}
     * 
     * @param id 수정할 게시글 ID
     * @param request 수정 내용
     * @return Mono<ResponseEntity<Post>> - 수정된 게시글 또는 404
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Post>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostCreateRequest request) {
        
        log.info("PUT /api/posts/{} - Updating post", id);
        
        return postService.updatePost(id, request)
                // 수정 성공 시 200 OK와 함께 반환
                .map(ResponseEntity::ok)
                // 게시글이 없으면 404 Not Found
                .defaultIfEmpty(ResponseEntity.notFound().build())
                // 에러 발생 시 500
                .onErrorResume(error -> {
                    log.error("Error updating post: {}", id, error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }
    
    /**
     * 게시글 삭제
     * DELETE /api/posts/{id}
     * 
     * @param id 삭제할 게시글 ID
     * @return Mono<ResponseEntity<Void>> - 204 No Content 또는 404
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePost(@PathVariable Long id) {
        log.info("DELETE /api/posts/{} - Deleting post", id);
        
        return postService.deletePost(id)
                // 삭제 성공 시 204 No Content 반환
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                // 게시글이 없거나 에러 발생 시 404
                .onErrorResume(error -> {
                    log.error("Error deleting post: {}", id, error);
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }
    
    /**
     * 게시글 개수 조회 (추가 API)
     * GET /api/posts/count
     * 
     * @return Mono<Long> - 전체 게시글 수
     */
    @GetMapping("/count")
    public Mono<Long> getPostCount() {
        log.info("GET /api/posts/count - Getting post count");
        return postService.getPostCount();
    }
}
