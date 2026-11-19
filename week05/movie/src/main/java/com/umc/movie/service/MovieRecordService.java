
package com.umc.movie.service;

import com.umc.movie.dto.CreateMovieRecordRequest;
import com.umc.movie.dto.MovieRecordResponse;
import com.umc.movie.entity.Favorite;
import com.umc.movie.entity.Member;
import com.umc.movie.entity.Rating;
import com.umc.movie.exception.ApiException;
import com.umc.movie.repository.FavoriteRepository;
import com.umc.movie.repository.MemberRepository;
import com.umc.movie.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieRecordService {

    private final MemberRepository memberRepository;
    private final RatingRepository ratingRepository;
    private final FavoriteRepository favoriteRepository;

    @Transactional
    public MovieRecordResponse createMovieRecord(
            Long memberId,
            CreateMovieRecordRequest request
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
            throw new ApiException(
                    HttpStatus.CONFLICT,
                    "RATING409_1",
                    "이미 이 영화에 평점을 남기셨습니다."
            );
        }

        Rating rating = Rating.builder()
                .member(member)
                .movieId(request.movieId())
                .score(request.score())
                .comment(request.comment())
                .build();

        Rating savedRating = ratingRepository.saveAndFlush(rating);

        boolean addedToFavorites = false;

        if (request.addToFavorites()) {
            if (!favoriteRepository.existsByMember_IdAndMovieId(
                    memberId, request.movieId()
            )) {
                Favorite favorite = Favorite.builder()
                        .member(member)
                        .movieId(request.movieId())
                        .build();

                favoriteRepository.saveAndFlush(favorite);
                addedToFavorites = true;
            }
        }

        return new MovieRecordResponse(
                savedRating.getId(),
                savedRating.getMovieId(),
                savedRating.getScore(),
                savedRating.getComment(),
                addedToFavorites
        );
    }
}
