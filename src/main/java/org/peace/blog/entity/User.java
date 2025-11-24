package org.peace.blog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 엔티티
 * - DB에 저장되는 관리자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User {
    
    @Id
    private Long id;
    private String role;
    private String username;  // 관리자 사용자명
    private String passcode;  // 패스코드 (해시 저장 권장)
    private String email;     // 인증번호 수신 이메일
}
