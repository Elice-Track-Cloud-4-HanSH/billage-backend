package com.team01.billage.chatting.controller;

import com.team01.billage.chatting.dto.*;
import com.team01.billage.chatting.service.ChatRedisService;
import com.team01.billage.chatting.service.ChatRoomService;
import com.team01.billage.chatting.service.ChatService;
import com.team01.billage.exception.CustomException;
import com.team01.billage.exception.ErrorCode;
import com.team01.billage.user.domain.CustomUserDetails;
import com.team01.billage.user.domain.Users;
import com.team01.billage.utils.DetermineUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
@Tag(name = "Chatroom", description = "채팅방 관련 API")
public class ChatRoomController {
    private final ChatRoomService chatroomService;
    private final ChatService chatService;
    private final DetermineUser determineUser;

    private final Set<String> chatTypes = new HashSet<>(Arrays.asList("ALL", "PR", "LENT", "RENT"));

    @GetMapping("/unread-chat")
    @Operation(summary = "읽지 않은 전체 채팅 개수 반환", description = "해당 유저의 읽지 않은 전체 채팅 개수를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "읽지 않은 채팅 개수가 없거나 로그인을 하지 않았다면 0을 반환합니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChatMessage.UnreadCount.class))
            ),
    })
    public ResponseEntity<ChatMessage.UnreadCount> getUnreadChatCount(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ChatMessage.UnreadCount unreadChatCountResponseDto = chatroomService.getUnreadChatCount(userDetails);
        return ResponseEntity.ok(unreadChatCountResponseDto);
    }

    @GetMapping
    @Operation(summary = "모든 채팅방 조회", description = "사용자의 모든 채팅방을 페이지네이션하여 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "읽지 않은 채팅 개수가 없거나 로그인을 하지 않았다면 0을 반환합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                        type = "array",
                                        implementation = ChatroomResponseDto.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode="400",
                    description="옳지 않은 채팅방 타입",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"올바르지 않은 채팅방 조회 타입입니다.\", \"code\": \"INVALID_CHAT_TYPE\"}")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "상품과 관련된 채팅방은 상품 ID가 필요합니다.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"상품 ID는 필수입니다.\", \"code\": \"PRODUCT_ID_REQUIRED\"}")
                    )
            ),

    })
    public ResponseEntity<List<ChatroomResponseDto>> getAllChatRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(name = "type", required = false, defaultValue="ALL") String type,
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "page", required = false, defaultValue="0") int page,
            @RequestParam(name = "pageSize", required = false, defaultValue="10") int pageSize
    ) {
        if (!chatTypes.contains(type)) {
            throw new CustomException(ErrorCode.INVALID_CHAT_TYPE);
        }

        Users user = determineUser.determineUser(userDetails);

        chatroomService.checkValidProductChatGetType(type, productId);

        List<ChatroomResponseDto> responseDto = chatroomService.getAllChatroomsWithDSL(type, page, pageSize, user.getId(), productId);
        return ResponseEntity.ok(responseDto);
    }


    @PostMapping("/valid")
    @Operation(summary = "채팅방 검증", description = "채팅방을 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "읽지 않은 채팅 개수가 없거나 로그인을 하지 않았다면 0을 반환합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = CheckValidChatroomResponseDto.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode="403",
                    description="채팅방에 속한 유저가 아닙니다.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"인증되지 않은 사용자입니다.\", \"code\": \"UNAUTHORIZED_USER\"}")
                    )
            ),
    })
    public ResponseEntity<CheckValidChatroomResponseDto> checkIsValidChatroom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CheckValidChatroomRequestDto checkValidChatroomDto
    ) {
        Users user = determineUser.determineUser(userDetails);

        CheckValidChatroomResponseDto responseDto = chatroomService.checkValidChatroom(checkValidChatroomDto, user);
        return ResponseEntity.ok(responseDto);
    }

    // 특정 채팅방의 이전 채팅 목록가져오기
    @GetMapping("/{chatroomId}")
    @Operation(summary = "채팅방의 채팅 기록 조회", description = "채팅방의 채팅 기록을 페이지네이션하여 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "채팅방의 채팅 기록을 반환합니다.",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(
                                            type = "array",
                                            implementation = ChatResponseDto.class)
                            )
                    )
            ),
            @ApiResponse(
                    responseCode="403",
                    description="채팅방에 속한 유저가 아닙니다.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"인증되지 않은 사용자입니다.\", \"code\": \"UNAUTHORIZED_USER\"}")
                    )
            ),
            @ApiResponse(
                    responseCode="404",
                    description="채팅방이 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"해당 채팅방을 찾을 수 없습니다.\", \"code\": \"CHATROOM_NOT_FOUND\"}")
                    )
            ),

    })
    public ResponseEntity<List<ChatResponseDto>> getAllChats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable(name = "chatroomId") Long chatroomId,
            @RequestParam(name = "page", defaultValue="0") int page,
            @RequestParam(name = "pageSize", defaultValue="50") int pageSize,
            @RequestParam(name = "lastLoadChatId", required = false, defaultValue = "" + Long.MAX_VALUE) Long lastLoadChatId
    ) {
        Users user = determineUser.determineUser(userDetails);

        List<ChatResponseDto> responseDto = chatroomService.getChatsInChatroom(chatroomId, user.getId(), page, pageSize, lastLoadChatId);
        return ResponseEntity.ok(responseDto);
    }

    @Transactional
    @PostMapping("/{chatroomId}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "읽음 처리 성공"
            ),
    })
    public ResponseEntity<Object> markAsRead(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "chatroomId") Long chatroomId) {
        Users user = determineUser.determineUser(userDetails);

        chatService.markAsRead(chatroomId, user);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping("/{chatroomId}")    
    @Operation(summary = "채팅방 나가기", description = "채팅방에서 나갑니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode="204",
                    description="채팅방에서 성공적으로 나갔습니다."
            ),
            @ApiResponse(
                    responseCode="403",
                    description="채팅방에 속한 유저가 아닙니다.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"인증되지 않은 사용자입니다.\", \"code\": \"UNAUTHORIZED_USER\"}")
                    )
            )
    })
    public ResponseEntity<Object> exitChatRoom(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable(name = "chatroomId") Long chatroomId) {
        Users user = determineUser.determineUser(userDetails);

        chatroomService.exitFromChatRoom(chatroomId, user.getId());
        return ResponseEntity.noContent().build();
    }
}
