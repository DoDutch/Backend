package graduation.project.DoDutch_server.domain.member.controller;


import graduation.project.DoDutch_server.domain.member.dto.response.MemberResponseDto;
import graduation.project.DoDutch_server.domain.member.service.MemberService;
import graduation.project.DoDutch_server.global.common.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /*
     * 개인정보 조회
     */
    @GetMapping
    public ApiResponse<MemberResponseDto> getMember(){
        MemberResponseDto responseDto = memberService.getMember();
        return ApiResponse.onSuccess(responseDto);
    }

//    @DeleteMapping
//    public ApiResponse<Void> deleteAccount() {
//        memberService.deleteMember();
//        return ApiResponse.onSuccess();
//    }
}
