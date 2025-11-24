package org.peace.blog.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.peace.blog.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 관리자 인증 API 컨트롤러
 */
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 인증번호 요청
     * POST /api/admin/auth/request
     */
    @PostMapping("/request")
    public Mono<ResponseEntity<ApiResponse>> requestVerificationCode(
            @RequestBody AuthRequest request) {
        
        return authService.requestVerificationCode(request.getUsername(), request.getPasscode())
            .map(message -> ResponseEntity.ok(new ApiResponse(true, message, null)))
            .onErrorResume(e -> Mono.just(
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage(), null))
            ));
    }
    
    /**
     * 인증번호 검증 및 JWT 발급
     * POST /api/admin/auth/verify
     */
    @PostMapping("/verify")
    public Mono<ResponseEntity<ApiResponse>> verifyCode(
            @RequestBody VerifyRequest request) {
        
        return authService.verifyCodeAndGenerateToken(request.getUsername(), request.getCode())
            .map(token -> ResponseEntity.ok(new ApiResponse(true, "인증 성공", token)))
            .onErrorResume(e -> Mono.just(
                ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage(), null))
            ));
    }
    
    /**
     * JWT 토큰 검증
     * GET /api/admin/verify-token
     */
    @GetMapping("/verify-token")
    public Mono<ResponseEntity<ApiResponse>> verifyToken(
            @RequestHeader("Authorization") String authHeader) {
        
        String token = authHeader.replace("Bearer ", "");
        
        return authService.validateToken(token)
            .map(valid -> {
                if (valid) {
                    return ResponseEntity.ok(new ApiResponse(true, "유효한 토큰입니다", null));
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "유효하지 않은 토큰입니다", null));
                }
            });
    }
    
    // DTO 클래스들
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthRequest {
        private String username;
        private String passcode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerifyRequest {
        private String username;
        private String code;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse {
        private boolean success;
        private String message;
        private String data;  // JWT 토큰
    }
}
