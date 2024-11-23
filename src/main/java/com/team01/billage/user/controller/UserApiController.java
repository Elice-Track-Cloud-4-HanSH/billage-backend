package com.team01.billage.user.controller;

import com.team01.billage.user.domain.Users;
import com.team01.billage.user.dto.Response.UserCheckIdResponseDto;
import com.team01.billage.user.dto.Response.UserCheckNicknameResponseDto;
import com.team01.billage.user.dto.Request.UserSignupRequestDto;
import com.team01.billage.user.dto.Response.UserDeleteResponseDto;
import com.team01.billage.user.dto.Response.UserSignupResponseDto;
import com.team01.billage.user.dto.UserDto;
import com.team01.billage.user.repository.UserRepository;
import com.team01.billage.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.team01.billage.config.jwt.UserConstants.ACCESS_TOKEN_TYPE_VALUE;
import static com.team01.billage.config.jwt.UserConstants.REFRESH_TOKEN_TYPE_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final UserRepository userRepository;

    //회원가입
    @Operation(summary = "회원가입 API", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserSignupResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = UserSignupResponseDto.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDto> signup(@RequestBody UserSignupRequestDto userSignupRequestDto) {

        if (userSignupRequestDto == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(UserSignupResponseDto.builder().message("bad request").build());
        }

        Users user = userService.save(userSignupRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserSignupResponseDto.builder().message("signup success").build());
    }


    //아이디 중복확인
    @Operation(summary = "이메일 중복 확인 API", description = "사용자가 입력한 이메일이 중복인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 이메일",
                    content = @Content(schema = @Schema(implementation = UserCheckIdResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일",
                    content = @Content(schema = @Schema(implementation = UserCheckIdResponseDto.class)))
    })
    @GetMapping("/signup/check-email")
    public ResponseEntity<UserCheckIdResponseDto> checkEmail(@RequestParam("userEmail") String userEmail) {
        boolean isUsernameTaken = userService.isDuplicateEmail(userEmail);

        if (isUsernameTaken) {
            // 아이디가 이미 존재하는 경우
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(UserCheckIdResponseDto.builder()
                            .isAvailable(false).message("이미 사용중인 아이디입니다.").build());
        }

        // 아이디 사용 가능
        return ResponseEntity.ok()
                .body(UserCheckIdResponseDto.builder()
                        .isAvailable(true).message("사용할 수 있는 아이디입니다.").build());
    }


    @Operation(summary = "닉네임 중복 확인 API", description = "사용자가 입력한 닉네임이 중복인지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임",
                    content = @Content(schema = @Schema(implementation = UserCheckNicknameResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임",
                    content = @Content(schema = @Schema(implementation = UserCheckNicknameResponseDto.class)))
    })
    @GetMapping("/signup/check-nickname")
    public ResponseEntity<UserCheckNicknameResponseDto> checkNickname(@RequestParam("nickname") String nickname) {
        boolean isNicknameTaken = userService.isDuplicateNickname(nickname);

        if (isNicknameTaken) {
            // 닉네임이 이미 존재하는 경우
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(UserCheckNicknameResponseDto.builder()
                            .isAvailable(false).message("이미 사용중인 닉네임입니다.").build());
        }

        // 닉네임 사용 가능
        return ResponseEntity.ok()
                .body(UserCheckNicknameResponseDto.builder()
                        .isAvailable(true).message("사용할 수 있는 닉네임입니다.").build());
    }



    @Operation(summary = "회원 탈퇴 API (소프트 삭제)", description = "이메일을 기반으로 회원을 소프트 삭제 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
                    content = @Content(schema = @Schema(implementation = UserDeleteResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "회원 탈퇴 실패",
                    content = @Content(schema = @Schema(implementation = UserDeleteResponseDto.class)))
    })
    //회원 탈퇴(soft delete)
    @DeleteMapping("/users-soft/{email}")
    public ResponseEntity<UserDeleteResponseDto> softDeleteUser(UserDto userDto,
                                                                @PathVariable String email,
                                                                HttpServletResponse response) {
        UserDeleteResponseDto userDeleteResponseDto = userService.softDeleteUser(userDto);

        if (userDeleteResponseDto.isDeleted()) {
            deleteCookie(REFRESH_TOKEN_TYPE_VALUE, response);
            deleteCookie(ACCESS_TOKEN_TYPE_VALUE, response);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(UserDeleteResponseDto.builder().message("회원탈퇴 성공").build());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(UserDeleteResponseDto.builder().message("오류 발생").build());
    }


    //쿠키 삭제 로직
    private void deleteCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie(token, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Lax");
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 즉시 만료
        response.addCookie(cookie);
    }

}
