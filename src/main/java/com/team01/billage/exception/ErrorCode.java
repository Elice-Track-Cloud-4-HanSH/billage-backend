package com.team01.billage.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    PRODUCT_MODIFICATION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "현재 대여 중인 상품은 수정/삭제할 수 없습니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    WRITE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "후기 작성 권한이 없습니다."),
    CHANGE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "변경 권한이 없습니다."),

    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    RENTAL_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 대여기록을 찾을 수 없습니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채팅방을 찾을 수 없습니다.");

    /* 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재, 조건을 만족하지 못함 */

    /* 410 : GONE : 리소스가 더 이상 유효하지 않음 */

    /* 500 INTERNAL_SERVER_ERROR : 서버 내부 에러 */

    private final HttpStatus httpStatus;
    private final String message;
}
