package org.peace.blog.repository;

import org.peace.blog.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Admin Repository
 * - R2DBC 기반 리액티브 저장소
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    
    /**
     * username으로 Admin 조회
     */
    Mono<User> findByUsername(String username);
}
