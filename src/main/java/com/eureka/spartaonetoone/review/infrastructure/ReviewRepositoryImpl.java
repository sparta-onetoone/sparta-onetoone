package com.eureka.spartaonetoone.review.infrastructure;

import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;
import com.eureka.spartaonetoone.review.domain.QReview;
import com.eureka.spartaonetoone.review.domain.Review;
import com.eureka.spartaonetoone.review.domain.repository.CustomReviewRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements CustomReviewRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReviewResponseDto> searchReviews(List<UUID> orderIds, Pageable pageable) {
        List<Review> reviews = queryFactory
                .selectFrom(QReview.review)
                .where(QReview.review.orderId.in(orderIds))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<ReviewResponseDto> dtos = reviews.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());

        return new PageImpl<ReviewResponseDto>(dtos, pageable, dtos.size());
    }
}
