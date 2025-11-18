package graduation.project.DoDutch_server.domain.member.controller;


import graduation.project.DoDutch_server.domain.member.dto.request.MemberUpdateRequestDto;
import graduation.project.DoDutch_server.domain.member.dto.response.MemberResponseDto;
import graduation.project.DoDutch_server.domain.member.service.MemberService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /*
     * 개인정보 조회
     */
    @GetMapping
    @Operation(summary = "개인정보 조회 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<MemberResponseDto> getMember(){
        MemberResponseDto responseDto = memberService.getMember();
        return ApiResponse.onSuccess(responseDto);
    }

    /*
     * 개인정보 수정
     */
    @PatchMapping
    @Operation(summary = "개인정보 수정 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<MemberResponseDto> updateMember(
            @RequestBody MemberUpdateRequestDto requestDto
    ){
        MemberResponseDto responseDto =  memberService.updateMember(requestDto);
        return ApiResponse.onSuccess(responseDto);
    }

    @DeleteMapping
    public ApiResponse<Void> deleteAccount() {
        memberService.deleteMember();
        return ApiResponse.onSuccess();
    }
}
