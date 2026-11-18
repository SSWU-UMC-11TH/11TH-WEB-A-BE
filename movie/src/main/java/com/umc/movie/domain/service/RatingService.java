package com.umc.movie.domain.service;

import com.umc.movie.domain.dto.request.CreateRatingRequest;
import com.umc.movie.domain.dto.request.UpdateRatingRequest;
import com.umc.movie.domain.dto.response.RatingResponse;
import com.umc.movie.domain.entity.Member;
import com.umc.movie.domain.entity.Rating;
import com.umc.movie.domain.repository.MemberRepository;
import com.umc.movie.domain.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Page<RatingResponse> getRatingsByMember(
            Long memberId,
            int page,
            int size
    ) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "page는 0 이상, size는 1~100이어야 합니다."
            );
        }

        if (!memberRepository.existsById(memberId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "존재하지 않는 유저입니다."
            );
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "ratingId")
        );

        return ratingRepository
                .findByMember_MemberId(memberId, pageable)
                .map(RatingResponse::from);
    }

    @Transactional
    public Long create(Long memberId, CreateRatingRequest request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 유저입니다."
                ));

        if (ratingRepository.existsByMember_MemberIdAndMovieId(
                memberId, request.movieId()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "이미 이 영화에 평점을 남기셨습니다."
            );
        }

        Rating rating = Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build();

        return ratingRepository.save(rating).getRatingId();
    }

    @Transactional
    public void update(
            Long ratingId,
            Long memberId,
            UpdateRatingRequest request
    ) {
        Rating rating = findOwnedRating(ratingId, memberId);

        rating.update(request.score(), request.comment());
    }

    @Transactional
    public void delete(Long ratingId, Long memberId) {

        Rating rating = findOwnedRating(ratingId, memberId);

        ratingRepository.delete(rating);
    }

    private Rating findOwnedRating(Long ratingId, Long memberId) {

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "존재하지 않는 평점입니다."
                ));

        if (!rating.getMember().getMemberId().equals(memberId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "본인이 작성한 평점만 수정하거나 삭제할 수 있습니다."
            );
        }

        return rating;
    }
}