package com.umc.movie.controller;

import com.umc.movie.dto.AvailabilityResponse;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.service.MemberService;
import com.umc.movie.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RatingService ratingService;

    @GetMapping("/nickname/{nickname}")
    public AvailabilityResponse checkNickname(
            @PathVariable String nickname
    ) {
        return memberService.checkNickname(nickname);
    }

    @GetMapping("/email/{email}")
    public AvailabilityResponse checkEmail(
            @PathVariable String email
    ) {
        return memberService.checkEmail(email);
    }

    @GetMapping("/{memberId}/ratings")
    public List<RatingResponse> getRatings(
            @PathVariable Long memberId
    ) {
        return ratingService.getRatingsByMember(memberId);
    }
}