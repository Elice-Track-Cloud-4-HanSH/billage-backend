package com.team01.billage.user.controller;

import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;
import static com.team01.billage.config.jwt.UserConstants.REFRESH_TOKEN_TYPE_VALUE;

import com.team01.billage.user.dto.Request.UserSignupRequestDto;
import com.team01.billage.user.dto.Response.TargetProfileResponseDto;
import com.team01.billage.user.dto.Response.UserDeleteResponseDto;
import com.team01.billage.user.dto.Response.UserResponseDto;
import com.team01.billage.user.dto.Response.UserSignupResponseDto;
import com.team01.billage.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserApiController {

    private final UserService userService;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "409", description = "이메일 또는 닉네임 중복")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDto> signup(
        @Valid @RequestBody UserSignupRequestDto signupRequest) {
        UserResponseDto userResponse = userService.signup(signupRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new UserSignupResponseDto("회원가입이 완료되었습니다."));
    }

    @Operation(summary = "이메일 중복 확인", description = "이메일 중복 여부를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
        @ApiResponse(responseCode = "409", description = "중복된 이메일")
    })
    @GetMapping("/signup/check-email")
    public ResponseEntity<EmailAvailabilityResponse> checkEmailAvailability(
        @RequestParam String email) {
        userService.validateEmail(email);
        return ResponseEntity.ok(new EmailAvailabilityResponse("사용 가능한 이메일입니다."));
    }

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 중복 여부를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임"),
        @ApiResponse(responseCode = "409", description = "중복된 닉네임")
    })
    @GetMapping("/signup/check-nickname")
    public ResponseEntity<NicknameAvailabilityResponse> checkNicknameAvailability(
        @RequestParam String nickname) {
        userService.validateNickname(nickname);
        return ResponseEntity.ok(new NicknameAvailabilityResponse("사용 가능한 닉네임입니다."));
    }

    @Operation(summary = "회원 탈퇴", description = "회원 정보를 소프트 삭제처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "탈퇴 처리 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/{email}")
    public ResponseEntity<UserDeleteResponseDto> deleteUser(
        @PathVariable String email,
        HttpServletResponse response) {
        UserDeleteResponseDto deleteResponse = userService.deleteUser(email);

        if (deleteResponse.isDeleted()) {
            clearAuthCookies(response);
            return ResponseEntity.ok(deleteResponse);
        }

        return ResponseEntity.badRequest().body(deleteResponse);
    }

    @GetMapping("/profile")
    public ResponseEntity<TargetProfileResponseDto> targetProfile(@RequestParam String nickname) {

        TargetProfileResponseDto responseDto = userService.showProfile(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    private void clearAuthCookies(HttpServletResponse response) {
        deleteCookie(ACCESS_TOKEN_TYPE_VALUE, response);
        deleteCookie(REFRESH_TOKEN_TYPE_VALUE, response);
    }

    private void deleteCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Lax");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}

// Response classes
@Schema(description = "이메일 사용 가능 여부 응답")
record EmailAvailabilityResponse(
    @Schema(description = "응답 메시지")
    String message
) {

}

@Schema(description = "닉네임 사용 가능 여부 응답")
record NicknameAvailabilityResponse(
    @Schema(description = "응답 메시지")
    String message
) {

}