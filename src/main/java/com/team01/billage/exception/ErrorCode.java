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
    PRODUCT_ID_REQUIRED(HttpStatus.BAD_REQUEST, "상품 ID는 필수입니다."),
    INVALID_QUERY_PARAMETER_TYPE(HttpStatus.BAD_REQUEST, "유효하지 않은 쿼리 파라미터 유형입니다."),
    INVALID_CHAT_TYPE(HttpStatus.BAD_REQUEST, "올바르지 않은 채팅방 조회 타입입니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "인증 완료되지 않은 이메일입니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "잘못된 인증 코드입니다."),
    EXPIRED_EMAIL_CODE(HttpStatus.UNAUTHORIZED, "만료된 인증 코드입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 REFRESH 토큰입니다."),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),

    /* 403 FORBIDDEN : 권한이 없는 사용자 */
    WRITE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "후기 작성 권한이 없습니다."),
    CHANGE_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "변경 권한이 없습니다."),
    CHATROOM_ACCESS_FORBIDDEN(HttpStatus.FORBIDDEN, "채팅방에 참여 중이지 않습니다."),
    NOT_PRODUCT_OWNER(HttpStatus.FORBIDDEN, "상품의 주인이 아닙니다."),
    UNAUTHORIZED_WEBSOCKET_CONNECTION(HttpStatus.FORBIDDEN, "웹소켓 연결을 다시 확인해주세요. 토큰이 있나요?"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    CHATROOM_VALIDATE_FAILED(HttpStatus.FORBIDDEN, "구매자 또는 판매자가 토큰의 값과 다릅니다."),

    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    RENTAL_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 대여기록을 찾을 수 없습니다."),
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채팅방을 찾을 수 없습니다."),
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 ID를 가진 채팅을 찾을 수 없습니다."),
    THUMBNAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "썸네일 이미지를 찾을 수 없습니다."),
    PRODUCT_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상품 이미지를 찾을 수 없습니다."),
    EMD_AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "행정 구역을 찾을 수 없습니다."),
    ACTIVITY_AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "활동 지역을 찾을 수 없습니다."),
    NEIGHBOR_AREA_NOT_FOUND(HttpStatus.NOT_FOUND, "이웃 지역을 찾을 수 없습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."),


    /* 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재, 조건을 만족하지 못함 */
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 유저가 이미 존재합니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 이메일이 이미 존재합니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 닉네임이 이미 존재합니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 거래에 대한 리뷰가 이미 존재합니다."),
    PRODUCT_ALREADY_RETURNED(HttpStatus.CONFLICT, "이미 반납이 완료된 상품입니다."),
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 좋아요를 한 상품입니다."),

    /* 410 : GONE : 리소스가 더 이상 유효하지 않음 */

    /* 500 INTERNAL_SERVER_ERROR : 서버 내부 에러 */
    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드 중 오류가 발생했습니다."),
    IO_EXCEPTION_ON_FILE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 삭제 중 오류가 발생했습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
