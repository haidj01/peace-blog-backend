package org.peace.blog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.peace.blog.entity.Post;
import org.peace.blog.repository.PostRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * PostService
 * 게시글 관련 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    /**
     * 게시글 생성
     */
    public Mono<Post> createPost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setViewCount(0);

        if (post.getStatus() == null) {
            post.setStatus("DRAFT");
        }

        if (post.getCommentEnabled() == null) {
            post.setCommentEnabled(true);
        }

        return postRepository.save(post)
            .doOnSuccess(p -> log.info("게시글 생성 완료: ID={}, 제목={}", p.getId(), p.getTitle()));
    }

    /**
     * 게시글 수정
     */
    public Mono<Post> updatePost(Long id, Post updatedPost) {
        return postRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("게시글을 찾을 수 없습니다")))
            .flatMap(existingPost -> {
                existingPost.setTitle(updatedPost.getTitle());
                existingPost.setContent(updatedPost.getContent());
                existingPost.setSummary(updatedPost.getSummary());
                existingPost.setCategory(updatedPost.getCategory());
                existingPost.setTags(updatedPost.getTags());
                existingPost.setCommentEnabled(updatedPost.getCommentEnabled());
                existingPost.setUpdatedAt(LocalDateTime.now());

                return postRepository.save(existingPost);
            })
            .doOnSuccess(p -> log.info("게시글 수정 완료: ID={}", p.getId()));
    }

    /**
     * 게시글 발행
     */
    public Mono<Post> publishPost(Long id) {
        return postRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("게시글을 찾을 수 없습니다")))
            .flatMap(post -> {
                post.setStatus("PUBLISHED");
                post.setPublishedAt(LocalDateTime.now());
                post.setUpdatedAt(LocalDateTime.now());
                return postRepository.save(post);
            })
            .doOnSuccess(p -> log.info("게시글 발행 완료: ID={}", p.getId()));
    }

    /**
     * 게시글 조회 (조회수 증가)
     */
    public Mono<Post> getPostById(Long id) {
        return postRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("게시글을 찾을 수 없습니다")))
            .flatMap(post -> {
                post.setViewCount(post.getViewCount() + 1);
                return postRepository.save(post);
            })
            .doOnSuccess(p -> log.info("게시글 조회: ID={}, 조회수={}", p.getId(), p.getViewCount()));
    }

    /**
     * 게시글 조회 (조회수 증가 없음)
     */
    public Mono<Post> getPostByIdWithoutIncrement(Long id) {
        return postRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("게시글을 찾을 수 없습니다")))
            .doOnSuccess(p -> log.info("게시글 조회 (조회수 증가 없음): ID={}", p.getId()));
    }

    /**
     * 게시글 삭제
     */
    public Mono<Void> deletePost(Long id) {
        return postRepository.findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("게시글을 찾을 수 없습니다")))
            .flatMap(post -> postRepository.delete(post))
            .doOnSuccess(v -> log.info("게시글 삭제 완료: ID={}", id));
    }

    /**
     * 전체 게시글 목록 조회 (최신순)
     */
    public Flux<Post> getAllPosts() {
        return postRepository.findAll()
            .sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .doOnComplete(() -> log.info("전체 게시글 목록 조회 완료"));
    }

    /**
     * 발행된 게시글 목록 조회 (최신순)
     */
    public Flux<Post> getPublishedPosts() {
        return postRepository.findByStatusOrderByCreatedAtDesc("PUBLISHED")
            .doOnComplete(() -> log.info("발행된 게시글 목록 조회 완료"));
    }

    /**
     * 작성자별 게시글 목록 조회
     */
    public Flux<Post> getPostsByAuthor(String username) {
        return postRepository.findByUsername(username)
            .sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .doOnComplete(() -> log.info("작성자별 게시글 목록 조회 완료: username={}", username));
    }

    /**
     * 카테고리별 게시글 목록 조회
     */
    public Flux<Post> getPostsByCategory(String category) {
        return postRepository.findByCategory(category)
            .sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .doOnComplete(() -> log.info("카테고리별 게시글 목록 조회 완료: category={}", category));
    }

    /**
     * 작성자 username과 상태로 게시글 목록 조회
     */
    public Flux<Post> getPostsByAuthorAndStatus(String username, String status) {
        return postRepository.findByUsernameAndStatus(username, status)
            .sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .doOnComplete(() -> log.info("작성자 및 상태별 게시글 목록 조회 완료: username={}, status={}", username, status));
    }

    /**
     * 전체 게시글 수 조회
     */
    public Mono<Long> getPostCount() {
        return postRepository.count()
            .doOnSuccess(count -> log.info("전체 게시글 수: {}", count));
    }
}
