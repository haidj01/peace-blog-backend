package org.peace.blog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.peace.blog.entity.Post;
import org.peace.blog.model.PostCreateRequest;
import org.peace.blog.model.PostUpdateRequest;
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
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 모든 게시글 조회
     * GET /api/posts
     */
    @GetMapping
    public Flux<Post> getAllPosts() {
        log.info("GET /api/posts - Getting all posts");
        return postService.getAllPosts();
    }

    /**
     * 발행된 게시글만 조회
     * GET /api/posts/published
     */
    @GetMapping("/published")
    public Flux<Post> getPublishedPosts() {
        log.info("GET /api/posts/published - Getting published posts");
        return postService.getPublishedPosts();
    }

    /**
     * 특정 게시글 조회 (조회수 증가)
     * GET /api/posts/{id}
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Post>> getPostById(@PathVariable Long id) {
        log.info("GET /api/posts/{} - Getting post by id", id);

        return postService.getPostById(id)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    log.error("Error getting post: {}", id, error);
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    /**
     * 작성자별 게시글 조회
     * GET /api/posts/author/{username}
     */
    @GetMapping("/author/{username}")
    public Flux<Post> getPostsByAuthor(@PathVariable String username) {
        log.info("GET /api/posts/author/{} - Getting posts by author", username);
        return postService.getPostsByAuthor(username);
    }

    /**
     * 카테고리별 게시글 조회
     * GET /api/posts/category/{category}
     */
    @GetMapping("/category/{category}")
    public Flux<Post> getPostsByCategory(@PathVariable String category) {
        log.info("GET /api/posts/category/{} - Getting posts by category", category);
        return postService.getPostsByCategory(category);
    }

    /**
     * 새 게시글 작성
     * POST /api/posts
     */
    @PostMapping
    public Mono<ResponseEntity<Post>> createPost(@Valid @RequestBody PostCreateRequest request) {
        log.info("POST /api/posts - Creating new post: {}", request.getTitle());

        // DTO를 Entity로 변환
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .summary(request.getSummary())
                .username(request.getUsername())
                .status(request.getStatus())
                .category(request.getCategory())
                .tags(request.getTags())
                .commentEnabled(request.getCommentEnabled())
                .build();

        return postService.createPost(post)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created))
                .onErrorResume(error -> {
                    log.error("Error creating post", error);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    /**
     * 게시글 수정
     * PUT /api/posts/{id}
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Post>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostUpdateRequest request) {

        log.info("PUT /api/posts/{} - Updating post", id);

        // DTO를 Entity로 변환
        Post updatedPost = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .summary(request.getSummary())
                .category(request.getCategory())
                .tags(request.getTags())
                .commentEnabled(request.getCommentEnabled())
                .build();

        return postService.updatePost(id, updatedPost)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    log.error("Error updating post: {}", id, error);
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    /**
     * 게시글 발행
     * POST /api/posts/{id}/publish
     */
    @PostMapping("/{id}/publish")
    public Mono<ResponseEntity<Post>> publishPost(@PathVariable Long id) {
        log.info("POST /api/posts/{}/publish - Publishing post", id);

        return postService.publishPost(id)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    log.error("Error publishing post: {}", id, error);
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    /**
     * 게시글 삭제
     * DELETE /api/posts/{id}
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePost(@PathVariable Long id) {
        log.info("DELETE /api/posts/{} - Deleting post", id);

        return postService.deletePost(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorResume(error -> {
                    log.error("Error deleting post: {}", id, error);
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }

    /**
     * 게시글 개수 조회
     * GET /api/posts/count
     */
    @GetMapping("/count")
    public Mono<Long> getPostCount() {
        log.info("GET /api/posts/count - Getting post count");
        return postService.getPostCount();
    }
}
