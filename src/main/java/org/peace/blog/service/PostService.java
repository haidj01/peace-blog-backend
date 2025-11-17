package org.peace.blog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.peace.blog.model.Post;
import org.peace.blog.model.PostCreateRequest;
import org.peace.blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * PostService
 * 게시글 관련 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor  // final 필드에 대한 생성자 자동 생성 (DI용)
public class PostService {
    
    // Repository 의존성 주입
    private final PostRepository postRepository;
    
    /**
     * 모든 게시글 조회
     * @return Flux<Post> - 모든 게시글 스트림
     */
    public Flux<Post> getAllPosts() {
        log.info("Getting all posts");
        
        return postRepository.findAll()
                // 최신 게시글이 먼저 오도록 ID 역순 정렬
                .sort((p1, p2) -> p2.getId().compareTo(p1.getId()))
                .doOnComplete(() -> log.info("Retrieved all posts"))
                .doOnError(error -> log.error("Error getting all posts", error));
    }
    
    /**
     * ID로 게시글 조회
     * @param id 게시글 ID
     * @return Mono<Post> - 조회된 게시글
     */
    public Mono<Post> getPostById(Long id) {
        log.info("Getting post by id: {}", id);
        
        return postRepository.findById(id)
                .doOnSuccess(post -> {
                    if (post != null) {
                        log.info("Found post: {}", id);
                    } else {
                        log.warn("Post not found: {}", id);
                    }
                })
                .doOnError(error -> log.error("Error getting post by id: {}", id, error));
    }
    
    /**
     * 새 게시글 작성
     * @param request 게시글 작성 요청 DTO
     * @return Mono<Post> - 생성된 게시글
     */
    public Mono<Post> createPost(PostCreateRequest request) {
        log.info("Creating new post with title: {}", request.getTitle());
        
        // DTO를 Entity로 변환
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .build();
        
        return postRepository.createPost(post)
                .doOnSuccess(created -> log.info("Created post with id: {}", created.getId()))
                .doOnError(error -> log.error("Error creating post", error));
    }
    
    /**
     * 게시글 수정
     * @param id 수정할 게시글 ID
     * @param request 수정 내용
     * @return Mono<Post> - 수정된 게시글
     */
    public Mono<Post> updatePost(Long id, PostCreateRequest request) {
        log.info("Updating post: {}", id);
        
        // DTO를 Entity로 변환
        Post updatedPost = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .build();
        
        return postRepository.updatePost(id, updatedPost)
                .doOnSuccess(updated -> {
                    if (updated != null) {
                        log.info("Updated post: {}", id);
                    } else {
                        log.warn("Post not found for update: {}", id);
                    }
                })
                .doOnError(error -> log.error("Error updating post: {}", id, error));
    }
    
    /**
     * 게시글 삭제
     * @param id 삭제할 게시글 ID
     * @return Mono<Void> - 완료 시그널
     */
    public Mono<Void> deletePost(Long id) {
        log.info("Deleting post: {}", id);
        
        // 먼저 게시글 존재 여부 확인
        return postRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Post not found: " + id)))
                .flatMap(post -> postRepository.deletePost(id))
                .doOnSuccess(v -> log.info("Deleted post: {}", id))
                .doOnError(error -> log.error("Error deleting post: {}", id, error));
    }
    
    /**
     * 전체 게시글 수 조회
     * @return Mono<Long> - 게시글 개수
     */
    public Mono<Long> getPostCount() {
        return postRepository.count()
                .doOnSuccess(count -> log.info("Total posts: {}", count));
    }
}
