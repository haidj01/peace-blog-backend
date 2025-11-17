package org.peace.blog.repository;

import lombok.extern.slf4j.Slf4j;
import org.peace.blog.model.Post;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * PostRepository
 * In-Memory 방식의 게시글 저장소 (Mock 데이터)
 * 실제 DB 없이 ConcurrentHashMap을 사용하여 데이터 관리
 */
@Slf4j  // 로깅을 위한 Lombok 어노테이션
@Repository
public class PostRepository {
    
    // Thread-safe한 Map으로 게시글 저장
    private final Map<Long, Post> posts = new ConcurrentHashMap<>();
    
    // Auto-increment ID 생성기
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * 생성자 - 초기 Mock 데이터 생성
     */
    public PostRepository() {
        initMockData();
    }
    
    /**
     * 초기 Mock 데이터 생성
     * 애플리케이션 시작 시 샘플 게시글 3개 생성
     */
    private void initMockData() {
        log.info("Initializing mock data...");
        
        // 샘플 게시글 1
        createPost(Post.builder()
                .title("Spring WebFlux 시작하기")
                .content("<h2>Spring WebFlux란?</h2><p>Spring WebFlux는 리액티브 프로그래밍을 지원하는 웹 프레임워크입니다.</p><p>Non-blocking I/O를 사용하여 높은 처리량과 확장성을 제공합니다.</p>")
                .author("관리자")
                .build())
                .subscribe(post -> log.info("Created mock post: {}", post.getId()));
        
        // 샘플 게시글 2
        createPost(Post.builder()
                .title("React와 Spring 연동하기")
                .content("<h2>프론트엔드와 백엔드 연동</h2><p>React 프론트엔드와 Spring 백엔드를 연동하는 방법을 알아봅니다.</p><ul><li>CORS 설정</li><li>REST API 설계</li><li>Axios를 통한 HTTP 통신</li></ul>")
                .author("개발자")
                .build())
                .subscribe(post -> log.info("Created mock post: {}", post.getId()));
        
        // 샘플 게시글 3
        createPost(Post.builder()
                .title("Peace Blog 시작합니다")
                .content("<h1>환영합니다!</h1><p>Peace Blog에 오신 것을 환영합니다.</p><p>이 블로그는 React + Spring WebFlux로 구현되었습니다.</p><img src='https://via.placeholder.com/600x300' alt='샘플 이미지'/>")
                .author("DJ")
                .build())
                .subscribe(post -> log.info("Created mock post: {}", post.getId()));
    }
    
    /**
     * 모든 게시글 조회
     * @return Flux<Post> - 게시글 스트림
     */
    public Flux<Post> findAll() {
        log.debug("Finding all posts, total: {}", posts.size());
        // Map의 값들을 Flux로 변환하여 반환
        return Flux.fromIterable(posts.values());
    }
    
    /**
     * ID로 게시글 조회
     * @param id 게시글 ID
     * @return Mono<Post> - 단일 게시글 (없으면 empty)
     */
    public Mono<Post> findById(Long id) {
        log.debug("Finding post by id: {}", id);
        Post post = posts.get(id);
        // post가 null이면 Mono.empty(), 있으면 Mono.just(post) 반환
        return post != null ? Mono.just(post) : Mono.empty();
    }
    
    /**
     * 게시글 생성
     * @param post 생성할 게시글 (ID 없음)
     * @return Mono<Post> - 생성된 게시글 (ID 포함)
     */
    public Mono<Post> createPost(Post post) {
        // ID 자동 생성
        Long newId = idGenerator.getAndIncrement();
        
        // 타임스탬프 설정
        LocalDateTime now = LocalDateTime.now();
        
        // 새 게시글 생성
        Post newPost = Post.builder()
                .id(newId)
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getAuthor())
                .createdAt(now)
                .updatedAt(now)
                .build();
        
        // Map에 저장
        posts.put(newId, newPost);
        
        log.debug("Created post with id: {}", newId);
        
        // Mono로 반환
        return Mono.just(newPost);
    }
    
    /**
     * 게시글 수정
     * @param id 수정할 게시글 ID
     * @param updatedPost 수정 내용
     * @return Mono<Post> - 수정된 게시글 (없으면 empty)
     */
    public Mono<Post> updatePost(Long id, Post updatedPost) {
        log.debug("Updating post with id: {}", id);
        
        Post existingPost = posts.get(id);
        
        // 게시글이 없으면 empty 반환
        if (existingPost == null) {
            log.warn("Post not found for update, id: {}", id);
            return Mono.empty();
        }
        
        // 기존 게시글 업데이트
        Post updated = Post.builder()
                .id(existingPost.getId())
                .title(updatedPost.getTitle())
                .content(updatedPost.getContent())
                .author(updatedPost.getAuthor())
                .createdAt(existingPost.getCreatedAt())
                .updatedAt(LocalDateTime.now())  // 수정 시간 갱신
                .build();
        
        posts.put(id, updated);
        
        log.debug("Updated post: {}", id);
        
        return Mono.just(updated);
    }
    
    /**
     * 게시글 삭제
     * @param id 삭제할 게시글 ID
     * @return Mono<Void> - 완료 시그널
     */
    public Mono<Void> deletePost(Long id) {
        log.debug("Deleting post with id: {}", id);
        
        Post removed = posts.remove(id);
        
        if (removed == null) {
            log.warn("Post not found for deletion, id: {}", id);
            // 게시글이 없어도 완료 처리
            return Mono.empty();
        }
        
        log.debug("Deleted post: {}", id);
        
        // Void를 반환하는 Mono (완료 시그널만 전달)
        return Mono.empty();
    }
    
    /**
     * 전체 게시글 수 조회
     * @return Mono<Long> - 게시글 개수
     */
    public Mono<Long> count() {
        return Mono.just((long) posts.size());
    }
}
