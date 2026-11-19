
package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Member not found"
            );
        }

        return ratingRepository.findByMemberIdOrderByIdDesc(memberId)
                .stream()
                .map(RatingResponse::from)
                .toList();
    }

    @Transactional
    public RatingResponse createRating(
            Long memberId, CreateRatingRequest request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."
                ));

        if (ratingRepository.existsByMemberIdAndMovieId(
                memberId, request.movieId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 평점을 등록한 영화입니다."
            );
        }

        Rating rating = Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build();

        try {
            Rating savedRating = ratingRepository.saveAndFlush(rating);
            return RatingResponse.from(savedRating);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "평점 저장 중 중복 또는 제약조건 위반이 발생했습니다.", e
            );
        }
    }

    @Transactional
    public RatingResponse updateRating(
            Long memberId, Long ratingId, UpdateRatingRequest request
    ) {
        Rating rating = findOwnedRating(memberId, ratingId);
        rating.update(request.score(), request.comment());

        return RatingResponse.from(rating);
    }

    @Transactional
    public void deleteRating(Long memberId, Long ratingId) {
        Rating rating = findOwnedRating(memberId, ratingId);
        ratingRepository.delete(rating);
    }

    private Rating findOwnedRating(Long memberId, Long ratingId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "평점을 찾을 수 없습니다."
                ));

        if (!rating.getMember().getId().equals(memberId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "다른 회원의 평점은 수정하거나 삭제할 수 없습니다."
            );
        }

        return rating;
    }
}
