package com.team01.billage.user.domain;

import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.dto.Response.UserDeleteResponseDto;
import com.team01.billage.user.dto.Response.UserResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private Provider provider;

    @Column(name = "deleted_at")
    private java.sql.Timestamp deletedAt;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role을 GrantedAuthority 형태로 변환
        return Collections.singleton(() -> "ROLE_" + this.role);
    }

    @Override
    public String getUsername() {
        return email; // UserDetails에서 username은 보통 email로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 로직을 정의하지 않을 경우 true
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 로직을 정의하지 않을 경우 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 로직을 정의하지 않을 경우 true
    }


    // Users 클래스 내부에 추가
    public UserResponseDto toResponseDto() {
        return UserResponseDto.builder()
                .id(this.id)
                .nickname(this.nickname)
                .email(this.email)
                .imageUrl(this.imageUrl)
                .description(this.description)
                .role((this.role)) // role은 String이므로 Enum으로 변환
                .provider(this.provider) // provider도 Enum으로 변환
                .build();
    }


    // 삭제 여부 확인 메서드
    public boolean isDeleted() {
        return this.deletedAt != null; // deletedAt이 null이 아니면 삭제된 상태
    }

    // 회원 삭제 메서드
    public UserDeleteResponseDto deleteUser() {
        // 이미 삭제된 경우
        if (this.isDeleted()) { // isDeleted 메서드를 활용
            throw new CustomException(ErrorCode.USER_ALREADY_DELETED);
        }

        // 삭제 처리: deletedAt에 현재 시간 저장
        this.deletedAt = Timestamp.valueOf(LocalDateTime.now());

        return UserDeleteResponseDto.builder()
                .isDeleted(true)
                .message("회원 삭제 성공")
                .build();
    }
}
