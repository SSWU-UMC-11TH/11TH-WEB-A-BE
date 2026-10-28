
package com.umc.movie.domain.repository;

import com.umc.movie.domain.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByMember_MemberIdOrderByRatingIdDesc(Long memberId);

    Page<Rating> findByMember_MemberId(
            Long memberId,
            Pageable pageable
    );
}

