package com.team01.billage.user.controller;

import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;
import static com.team01.billage.config.jwt.UserConstants.REFRESH_TOKEN_TYPE_VALUE;

import com.team01.billage.exception.ErrorResponseEntity;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.dto.Request.EmailRequest;
import com.team01.billage.user.dto.Request.EmailVerificationRequest;
import com.team01.billage.user.dto.Request.ProfileUpdateRequest;
import com.team01.billage.user.dto.Request.UserPasswordRequestDto;
import com.team01.billage.user.dto.Request.UserSignupRequestDto;
import com.team01.billage.user.dto.Response.EmailAvailabilityResponse;
import com.team01.billage.user.dto.Response.NicknameAvailabilityResponse;
import com.team01.billage.user.dto.Response.ProfileResponse;
import com.team01.billage.user.dto.Response.SimpleUserInfoResponseDto;
import com.team01.billage.user.dto.Response.TargetProfileResponseDto;
import com.team01.billage.user.dto.Response.UserDeleteResponseDto;
import com.team01.billage.user.dto.Response.UserPasswordResponseDto;
import com.team01.billage.user.dto.Response.UserResponseDto;
import com.team01.billage.user.dto.Response.UserSignupResponseDto;
import com.team01.billage.user.service.ProfileService;
import com.team01.billage.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final ProfileService profileService;

    @PostMapping("/after-login")
    public ResponseEntity<SimpleUserInfoResponseDto> getSimpleUserInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        SimpleUserInfoResponseDto simpleUserInfoDto = new SimpleUserInfoResponseDto(userDetails);
        return ResponseEntity.ok(simpleUserInfoDto);
    }

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
    @DeleteMapping("")
    public ResponseEntity<UserDeleteResponseDto> deleteUser(
        @AuthenticationPrincipal UserDetails userDetails,
        HttpServletResponse response) {
        UserDeleteResponseDto deleteResponse = userService.deleteUser(userDetails.getUsername());

        if (deleteResponse.isDeleted()) {
            clearAuthCookies(response);
            return ResponseEntity.ok(deleteResponse);
        }

        return ResponseEntity.badRequest().body(deleteResponse);
    }

    @Operation(
        summary = "유저 조회",
        description = "해당 유저에 대한 프로필 정보들을 조회합니다.",
        tags = {"유저"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "유저 프로필 정보 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = TargetProfileResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 id에 대한 유저를 찾을 수 없는 경우",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
    })
    @GetMapping("/profile/{userId}")
    public ResponseEntity<TargetProfileResponseDto> targetProfile(
        @Parameter(description = "유저 ID", example = "12")
        @PathVariable("userId") long userId) {

        TargetProfileResponseDto responseDto = userService.showProfile(userId);
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

    @Operation(summary = "비밀번호 확인", description = "회원 탈퇴 전 비밀번호를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비밀번호 확인 성공"),
        @ApiResponse(responseCode = "401", description = "비밀번호 불일치"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/check-password")
    public ResponseEntity<UserPasswordResponseDto> checkPassword(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody UserPasswordRequestDto requestDto
    ) {
        UserPasswordResponseDto response = userService.verifyPassword(
            userDetails.getUsername(),  // email
            requestDto.password()
        );

        return response.matches()
            ? ResponseEntity.ok(response)
            : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 이메일 인증 코드 발송
    @PostMapping("/email-verification")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody EmailRequest request) {
        userService.sendVerificationEmail(request.getEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }

    // 비밀번호 찾기용  이메일 인증 코드 발송
    @PostMapping("/email-verification-password")
    public ResponseEntity<String> sendVerificationEmailForPassword(
        @RequestBody EmailRequest request) {
        userService.sendVerificationEmailForPassword(request.getEmail());
        return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
    }

    // 이메일 인증 코드 확인
    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody EmailVerificationRequest request) {
        userService.verifyEmail(request.getEmail(), request.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    @Operation(
        summary = "유저 조회",
        description = "요청을 보낸 사용자에 대한 프로필 정보들을 조회합니다.",
        tags = {"유저"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "유저 프로필 정보 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = TargetProfileResponseDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\"message\":\"인증되지 않은 사용자입니다.\",\"code\":\"UNAUTHORIZED_USER\"}"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "해당 id에 대한 유저를 찾을 수 없는 경우",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 에러",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseEntity.class))
        ),
    })
    @GetMapping("/get-profile")
    public ResponseEntity<ProfileResponse> getProfile(
        @Parameter(hidden = true)
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ProfileResponse response = profileService.getProfile(customUserDetails);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/update-profile")  // consumes 부분 제거
    public ResponseEntity<ProfileResponse> updateProfile(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestPart("request") ProfileUpdateRequest request,  // @ModelAttribute 대신 @RequestPart 사용
        @RequestPart(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        ProfileResponse response = profileService.updateProfile(customUserDetails.getEmail(),
            request, imageFile);
        return ResponseEntity.ok(response);
    }
}

