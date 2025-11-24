package org.peace.blog.repository;

import org.peace.blog.entity.Post;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Post Repository
 * - R2DBC 기반 리액티브 저장소
 */
@Repository
public interface PostRepository extends ReactiveCrudRepository<Post, Long> {

    /**
     * 작성자 username으로 게시글 목록 조회
     */
    Flux<Post> findByUsername(String username);

    /**
     * 상태별 게시글 목록 조회
     */
    Flux<Post> findByStatus(String status);

    /**
     * 카테고리별 게시글 목록 조회
     */
    Flux<Post> findByCategory(String category);

    /**
     * 작성자 username과 상태로 게시글 목록 조회
     */
    Flux<Post> findByUsernameAndStatus(String username, String status);

    /**
     * 상태별 게시글을 생성일시 역순으로 조회
     */
    Flux<Post> findByStatusOrderByCreatedAtDesc(String status);
}
