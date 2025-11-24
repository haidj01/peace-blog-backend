package org.peace.blog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 이메일 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    /**
     * 인증번호 이메일 발송 (리액티브)
     */
    public Mono<Void> sendVerificationCode(String toEmail, String code) {
        return Mono.fromRunnable(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject("[Peace Blog] 관리자 인증번호");
                message.setText(
                    "안녕하세요.\n\n" +
                    "Peace Blog 관리자 인증번호는 다음과 같습니다:\n\n" +
                    code + "\n\n" +
                    "이 인증번호는 5분간 유효합니다.\n\n" +
                    "Peace Blog 팀"
                );
                
                mailSender.send(message);
                log.info("인증번호 이메일 발송 완료: {}", toEmail);
            } catch (Exception e) {
                log.error("이메일 발송 실패: {}", toEmail, e);
                throw new RuntimeException("이메일 발송 실패", e);
            }
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }
}
