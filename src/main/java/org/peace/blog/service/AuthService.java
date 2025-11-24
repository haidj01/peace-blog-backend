package org.peace.blog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.peace.blog.repository.UserRepository;
import org.peace.blog.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 관리자 인증 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    // BCrypt 패스워드 인코더
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 인증번호 저장소 (username -> VerificationData)
    private final Map<String, VerificationData> verificationStore = new ConcurrentHashMap<>();

    private static final int CODE_LENGTH = 6;
    private static final Duration CODE_EXPIRATION = Duration.ofMinutes(5);
    
    /**
     * 인증번호 요청
     * - username과 passcode 검증 후 이메일 발송
     */
    public Mono<String> requestVerificationCode(String username, String passcode) {
        return userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(new RuntimeException("존재하지 않는 사용자입니다")))
            .flatMap(user -> {
                // BCrypt를 사용한 패스코드 검증
                if (!passwordEncoder.matches(passcode, user.getPasscode())) {
                    return Mono.error(new RuntimeException("패스코드가 일치하지 않습니다"));
                }

                // 인증번호 생성
                String code = generateVerificationCode();

                // 저장소에 저장
                verificationStore.put(username, new VerificationData(code, LocalDateTime.now()));

                log.info("인증번호 생성: {} -> {}", username, code);

                // 이메일 발송
                return emailService.sendVerificationCode(user.getEmail(), code)
                    .thenReturn("인증번호가 이메일로 발송되었습니다");
            });
    }
    
    /**
     * 인증번호 검증 및 JWT 발급
     */
    public Mono<String> verifyCodeAndGenerateToken(String username, String code) {
        VerificationData data = verificationStore.get(username);
        
        if (data == null) {
            return Mono.error(new RuntimeException("인증번호 요청 내역이 없습니다"));
        }
        
        // 만료 시간 확인
        if (Duration.between(data.getCreatedAt(), LocalDateTime.now()).compareTo(CODE_EXPIRATION) > 0) {
            verificationStore.remove(username);
            return Mono.error(new RuntimeException("인증번호가 만료되었습니다"));
        }
        
        // 인증번호 확인
        if (!data.getCode().equals(code)) {
            return Mono.error(new RuntimeException("인증번호가 일치하지 않습니다"));
        }
        
        // 사용된 인증번호 제거
        verificationStore.remove(username);
        
        // JWT 생성
        return jwtUtil.generateToken(username)
            .doOnSuccess(r -> log.info("JWT 발급 완료: {}", username) );

    }
    
    /**
     * JWT 토큰 검증
     */
    public Mono<Boolean> validateToken(String token) {
        return Mono.just(jwtUtil.validateToken(token));
    }
    
    /**
     * 6자리 랜덤 인증번호 생성
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 패스코드를 BCrypt로 해시화
     * (데이터베이스 저장 시 사용)
     */
    public String encodePasscode(String rawPasscode) {
        return passwordEncoder.encode(rawPasscode);
    }
    
    /**
     * 인증번호 데이터 내부 클래스
     */
    private static class VerificationData {
        private final String code;
        private final LocalDateTime createdAt;
        
        public VerificationData(String code, LocalDateTime createdAt) {
            this.code = code;
            this.createdAt = createdAt;
        }
        
        public String getCode() {
            return code;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
