package com.eureka.spartaonetoone.review.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.eureka.spartaonetoone.mock.MockUser;
import com.eureka.spartaonetoone.review.application.dtos.request.ReviewRequestDto;
import com.eureka.spartaonetoone.review.application.dtos.response.ReviewResponseDto;
import com.eureka.spartaonetoone.review.application.exception.ReviewException;
import com.eureka.spartaonetoone.review.domain.Review;
import com.eureka.spartaonetoone.review.domain.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest{

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateReview() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        ReviewRequestDto requestDto = new ReviewRequestDto(orderId, 5, "Great review", "http://image.url");
        Review review = requestDto.createReview();
        // Reflection을 사용해 reviewId와 createdAt 설정
        UUID reviewId = UUID.randomUUID();
        ReflectionTestUtils.setField(review, "reviewId", reviewId);
        ReflectionTestUtils.setField(review, "createdAt", LocalDateTime.now());
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // Act
        ReviewResponseDto responseDto = reviewService.createReview(requestDto);

        // Assert
        assertNotNull(responseDto);
        assertEquals(reviewId, responseDto.getReviewId());
        assertEquals("Great review", responseDto.getContent());
    }

    @Test


    public void testGetReviewById_Found() {
        UUID reviewId = UUID.randomUUID();
        Review review = Review.createReview(UUID.randomUUID(), 4, "Nice", "http://image.url");
        ReflectionTestUtils.setField(review, "reviewId", reviewId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        ReviewResponseDto responseDto = reviewService.getReviewById(reviewId);

        assertNotNull(responseDto);
        assertEquals(reviewId, responseDto.getReviewId());
    }

    @Test
    public void testGetReviewById_NotFound() {
        UUID reviewId = UUID.randomUUID();
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(ReviewException.ReviewNotFoundException.class, () -> {
            reviewService.getReviewById(reviewId);
        });
    }

    @Test
    public void testGetReviewsByOrderId() {
        UUID orderId = UUID.randomUUID();
        Review review1 = Review.createReview(orderId, 5, "Excellent", "http://img1.url");
        Review review2 = Review.createReview(orderId, 3, "Okay", "http://img2.url");
        ReflectionTestUtils.setField(review1, "reviewId", UUID.randomUUID());
        ReflectionTestUtils.setField(review2, "reviewId", UUID.randomUUID());
        List<Review> reviews = List.of(review1, review2);
        when(reviewRepository.findByOrderId(orderId)).thenReturn(reviews);
        Pageable pageable = Pageable.unpaged();

        Page<ReviewResponseDto> result = reviewService.getReviewsByOrderId(orderId, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        List<String> contents = result.getContent().stream().map(ReviewResponseDto::getContent).collect(Collectors.toList());
        assertTrue(contents.contains("Excellent"));
        assertTrue(contents.contains("Okay"));
    }

    @Test
    public void testUpdateReview() {
        UUID reviewId = UUID.randomUUID();
        Review existingReview = Review.createReview(UUID.randomUUID(), 3, "Old content", "old_image");
        ReflectionTestUtils.setField(existingReview, "reviewId", reviewId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArguments()[0]);

        ReviewRequestDto requestDto = new ReviewRequestDto(existingReview.getOrderId(), 5, "New content", "new_image");

        ReviewResponseDto responseDto = reviewService.updateReview(reviewId, requestDto);

        assertNotNull(responseDto);
        assertEquals("New content", responseDto.getContent());
        assertEquals(5, responseDto.getRating());
    }

    @Test
    public void testDeleteReview() {
        UUID reviewId = UUID.randomUUID();
        Review existingReview = Review.createReview(UUID.randomUUID(), 3, "Content", "image");
        ReflectionTestUtils.setField(existingReview, "reviewId", reviewId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(existingReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArguments()[0]);

        reviewService.deleteReview(reviewId);
        // 삭제 시 markDeleted()를 호출하여 deletedAt 필드가 설정되어야 함
        assertNotNull(ReflectionTestUtils.getField(existingReview, "deletedAt"));
    }

    @Test
    public void testSearchReviewsByOrderIds() {
        List<UUID> orderIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        Review review = Review.createReview(orderIds.get(0), 4, "Good review", "http://img.url");
        ReflectionTestUtils.setField(review, "reviewId", UUID.randomUUID());
        List<Review> reviews = List.of(review);
        PageImpl<ReviewResponseDto> page = new PageImpl<>(
                reviews.stream().map(ReviewResponseDto::from).collect(Collectors.toList())
        );
        when(reviewRepository.searchReviews(orderIds, Pageable.unpaged())).thenReturn(page);

        Page<ReviewResponseDto> result = reviewService.searchReviewsByOrderIds(orderIds, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Good review", result.getContent().get(0).getContent());
    }
}
