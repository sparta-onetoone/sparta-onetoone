package com.eureka.spartaonetoone.payment.infrastructure;

import com.eureka.spartaonetoone.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.payment.application.dtos.PaymentSearchDto;
import com.eureka.spartaonetoone.payment.domain.Payment;
import com.eureka.spartaonetoone.payment.domain.repository.CustomPaymentRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.eureka.spartaonetoone.domain.payment.domain.QPayment.payment;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements CustomPaymentRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<PaymentGetResponseDto> getPayments(final Pageable pageable) {
        List<Payment> payments = jpaQueryFactory.selectFrom(payment)
                .from(payment)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PaymentGetResponseDto> paymentGetResponseDtos = payments.stream()
                .map(PaymentGetResponseDto::from)
                .toList();

        return new PageImpl<>(paymentGetResponseDtos, pageable, paymentGetResponseDtos.size());

    }

    @Override
    public Page<PaymentGetResponseDto> searchPayments(final PaymentSearchDto paymentSearchDto, final Pageable pageable) {
        BooleanExpression booleanExpression = searchByCondition(paymentSearchDto);

        List<Payment> payments = jpaQueryFactory.selectFrom(payment)
                .from(payment)
                .where(searchByCondition(paymentSearchDto))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PaymentGetResponseDto> paymentGetResponseDtos = payments
                .stream()
                .map(PaymentGetResponseDto::from)
                .toList();

        return new PageImpl<>(paymentGetResponseDtos, pageable, paymentGetResponseDtos.size());
    }

    public BooleanExpression searchByPrice(final PaymentSearchDto paymentSearchDto) {
        return paymentSearchDto.getPrice() != null ? payment.price.goe(paymentSearchDto.getPrice()) : null;
    }

    public BooleanExpression searchByBank(final PaymentSearchDto paymentSearchDto) {
        return StringUtils.hasText(paymentSearchDto.getBank()) ? payment.bank.contains(paymentSearchDto.getBank()) : null;
    }

    public BooleanExpression searchByState(final PaymentSearchDto paymentSearchDto) {
        return StringUtils.hasText(paymentSearchDto.getState()) ? payment.state.contains(paymentSearchDto.getState()) : null;
    }

    public BooleanExpression searchByCondition(final PaymentSearchDto paymentSearchDto) {
        BooleanExpression predicate = null;

        BooleanExpression pricePredicate = searchByPrice(paymentSearchDto);
        if (pricePredicate != null) {
            predicate = pricePredicate;
        }

        BooleanExpression bankPredicate = searchByBank(paymentSearchDto);
        if (bankPredicate != null) {
            predicate = (predicate != null) ? predicate.and(bankPredicate) : bankPredicate;
        }

        BooleanExpression statePredicate = searchByState(paymentSearchDto);
        if (statePredicate != null) {
            predicate = (predicate != null) ? predicate.and(statePredicate) : statePredicate;
        }

        return predicate;
    }


}
