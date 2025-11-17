package org.peace.blog.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS 설정 프로퍼티
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cors")
class CorsProperties {
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    private boolean allowCredentials;
}

/**
 * WebConfig
 * WebFlux CORS 설정
 * React 프론트엔드와의 통신을 위한 CORS 허용 설정
 */
@Slf4j
@Configuration
public class WebConfig {

    private final CorsProperties corsProperties;

    public WebConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }
    
    /**
     * CORS 필터 빈 생성
     * 프론트엔드에서 백엔드 API 호출 시 CORS 에러 방지
     * 
     * @return CorsWebFilter
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        log.info("Configuring CORS filter");
        log.info("Allowed origins: {}", corsProperties.getAllowedOrigins());
        log.info("Allowed methods: {}", corsProperties.getAllowedMethods());

        CorsConfiguration corsConfig = new CorsConfiguration();

        // 허용할 출처 (프론트엔드 도메인)
        corsConfig.setAllowedOrigins(corsProperties.getAllowedOrigins());

        // 허용할 HTTP 메서드
        corsConfig.setAllowedMethods(corsProperties.getAllowedMethods());

        // 허용할 헤더
        corsConfig.setAllowedHeaders(corsProperties.getAllowedHeaders());

        // 쿠키/인증 정보 허용 여부
        corsConfig.setAllowCredentials(corsProperties.isAllowCredentials());

        // preflight 요청 캐시 시간 (초)
        corsConfig.setMaxAge(3600L);

        // URL 패턴별 CORS 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);  // 모든 경로에 적용

        return new CorsWebFilter(source);
    }
}
