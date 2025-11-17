package org.peace.blog;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

/**
 * PeaceBlogApplication
 * Spring Boot WebFlux 애플리케이션 진입점
 * 
 * @SpringBootApplication은 다음을 포함:
 * - @Configuration: Bean 설정 클래스
 * - @EnableAutoConfiguration: 자동 설정 활성화
 * - @ComponentScan: 컴포넌트 자동 스캔
 */
@Slf4j
@SpringBootApplication
public class PeaceBlogApplication {
    
    /**
     * 애플리케이션 시작점
     * @param args 커맨드 라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(PeaceBlogApplication.class, args);
    }
    
    /**
     * 애플리케이션 시작 완료 이벤트 리스너
     * 서버가 준비되면 실행됨
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("=================================================");
        log.info("Peace Blog Backend is ready!");
        log.info("Server running on: http://localhost:8080");
        log.info("API Base Path: http://localhost:8080/api");
        log.info("=================================================");
        log.info("Available Endpoints:");
        log.info("  GET    /api/posts           - 모든 게시글 조회");
        log.info("  GET    /api/posts/{id}      - 특정 게시글 조회");
        log.info("  POST   /api/posts           - 게시글 작성");
        log.info("  PUT    /api/posts/{id}      - 게시글 수정");
        log.info("  DELETE /api/posts/{id}      - 게시글 삭제");
        log.info("  POST   /api/images/upload   - 이미지 업로드");
        log.info("=================================================");
    }
}
