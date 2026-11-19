package com.umc.movie.service;

import com.umc.movie.dto.CreateRatingRequest;
import com.umc.movie.dto.RatingResponse;
import com.umc.movie.dto.UpdateRatingRequest;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ApiException(
                    HttpStatus.NOT_FOUND,
                    "MEMBER404_1",
                    "해당 회원을 찾을 수 없습니다."
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
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "MEMBER404_1",
                        "해당 회원을 찾을 수 없습니다."
                ));

        if (ratingRepository.existsByMemberIdAndMovieId(
                memberId, request.movieId()
        )) {
            throw duplicateRating();
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
            throw duplicateRating();
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
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "RATING404_1",
                        "해당 평점을 찾을 수 없습니다."
                ));

        if (!rating.getMember().getId().equals(memberId)) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "RATING403_1",
                    "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다."
            );
        }

        return rating;
    }

    private ApiException duplicateRating() {
        return new ApiException(
                HttpStatus.CONFLICT,
                "RATING409_1",
                "이미 이 영화에 평점을 남기셨습니다."
        );
    }
}