package com.team01.billage.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    PRODUCT_MODIFICATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "현재 대여 중인 상품은 수정/삭제할 수 없습니다."),
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 회원입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 올바르지 않습니다."),
    EMPTY_LOGIN_REQUEST(HttpStatus.BAD_REQUEST, "이메일 또는 비밀번호가 비어있습니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "잘못된 인증 코드입니다."),
    EXPIRED_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "만료된 인증 코드입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 REFRESH 토큰입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    WRITE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "후기 작성 권한이 없습니다."),
    CHANGE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "변경 권한이 없습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    RENTAL_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 대여기록을 찾을 수 없습니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채팅방을 찾을 수 없습니다."),
    THUMBNAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "썸네일 이미지를 찾을 수 없습니다."),
    PRODUCT_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND,"해당 상품 이미지를 찾을 수 없습니다."),
    EMD_AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "행정 구역을 찾을 수 없습니다."),
    ACTIVITY_AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "활동 지역을 찾을 수 없습니다."),

    /* 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재, 조건을 만족하지 못함 */
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT,"해당 유저가 이미 존재합니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT,"해당 이메일이 이미 존재합니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 닉네임이 이미 존재합니다."),

    /* 410 : GONE : 리소스가 더 이상 유효하지 않음 */

    /* 500 INTERNAL_SERVER_ERROR : 서버 내부 에러 */
    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드 중 오류가 발생했습니다."),
    IO_EXCEPTION_ON_FILE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제 중 오류가 발생했습니다.");



    private final HttpStatus httpStatus;
    private final String message;
}
