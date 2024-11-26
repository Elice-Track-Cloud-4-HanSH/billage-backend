package com.team01.billage.user.controller;

import com.team01.billage.user.domain.UserRole;
import com.team01.billage.user.dto.Request.JwtTokenLoginRequest;
import com.team01.billage.user.dto.Response.JwtTokenResponse;
import com.team01.billage.user.dto.Response.UserValidateTokenResponseDto;
import com.team01.billage.user.service.AuthenticationFacade;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

// TokenApiControllerTest.java (단위 테스트)
@ExtendWith(MockitoExtension.class)
class TokenApiControllerTest {

    @InjectMocks
    private TokenApiController tokenApiController;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        // Given
        JwtTokenLoginRequest loginRequest = createLoginRequest("test@email.com", "password");
        JwtTokenResponse expectedResponse = createSuccessResponse();

        given(authenticationFacade.handleLogin(any(), any(), any()))
                .willReturn(ResponseEntity.ok(expectedResponse));

        // When
        ResponseEntity<JwtTokenResponse> result = tokenApiController.login(loginRequest, response, null);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("로그인 성공");
        assertThat(result.getBody().getRole()).isEqualTo(UserRole.USER);

        verify(authenticationFacade).handleLogin(eq(loginRequest), eq(response), isNull());
    }

    @Test
    @DisplayName("토큰 검증 성공 테스트")
    void validateTokenSuccess() {
        // Given
        String validToken = "valid.token.string";
        UserValidateTokenResponseDto expectedResponse = UserValidateTokenResponseDto.builder()
                .message("success")
                .build();

        given(authenticationFacade.validateProtectedResource(validToken))
                .willReturn(ResponseEntity.ok(expectedResponse));

        // When
        ResponseEntity<UserValidateTokenResponseDto> result =
                tokenApiController.validateToken(validToken);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getMessage()).isEqualTo("success");

        verify(authenticationFacade).validateProtectedResource(validToken);
    }

    private JwtTokenLoginRequest createLoginRequest(String email, String password) {
        return new JwtTokenLoginRequest(email, password);
    }

    private JwtTokenResponse createSuccessResponse() {
        return JwtTokenResponse.builder()
                .accessToken("test.access.token")
                .role(UserRole.USER)
                .message("로그인 성공")
                .build();
    }
}