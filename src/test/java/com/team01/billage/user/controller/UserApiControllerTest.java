package com.team01.billage.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.Request.UserSignupRequestDto;
import com.team01.billage.user.dto.Response.UserSignupResponseDto;
import com.team01.billage.user.repository.UserRepository;
import com.team01.billage.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when; // when 메서드 임포트

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.any;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(UserApiController.class)
class UserApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserService userService;



    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    @DisplayName("회원가입 성공 테스트")
    void signup_success() throws Exception {
        // Given
        UserSignupRequestDto signupRequest = UserSignupRequestDto.builder()
                .email("test@example.com")
                .nickname("testuser")
                .password("password123")
                .build();

        Users savedUser = Users.builder()
                .email(signupRequest.getEmail())
                .nickname(signupRequest.getNickname())
                .password(signupRequest.getPassword())
                .build();

        given(userService.save(any(UserSignupRequestDto.class))).willReturn(savedUser);

        // JSON 요청 본문 생성
        String requestBody = new ObjectMapper().writeValueAsString(signupRequest);

        // When & Then
        mockMvc.perform(post("/api/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("signup success"));
    }

    @Test
    void checkEmail() {
    }

    @Test
    void checkNickname() {
    }

    @Test
    void softDeleteUser() {
    }

}