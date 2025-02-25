package com.eureka.spartaonetoone.payment.infrastructure;

import com.eureka.spartaonetoone.payment.application.dtos.PaymentGetResponseDto;
import com.eureka.spartaonetoone.payment.domain.Payment;
import com.eureka.spartaonetoone.payment.domain.QPayment;
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

import static com.eureka.spartaonetoone.payment.domain.QPayment.payment;


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
                .orderBy(payment.createdAt.asc(), payment.updatedAt.asc())
                .fetch();

        List<PaymentGetResponseDto> paymentGetResponseDtos = payments.stream()
                .map(PaymentGetResponseDto::from)
                .toList();

        return new PageImpl<>(paymentGetResponseDtos, pageable, paymentGetResponseDtos.size());

    }

    @Override
    public Page<PaymentGetResponseDto> searchPayments(String bank, Payment.State state, Integer price, final Pageable pageable) {


        List<Payment> payments = jpaQueryFactory.selectFrom(QPayment.payment)
                .from(payment)
                .where(searchByPrice(price), searchByBank(bank), searchByState(state))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(payment.createdAt.asc(), payment.updatedAt.asc())
                .fetch();

        List<PaymentGetResponseDto> paymentGetResponseDtos = payments
                .stream()
                .map(PaymentGetResponseDto::from)
                .toList();

        return new PageImpl<>(paymentGetResponseDtos, pageable, paymentGetResponseDtos.size());
    }

    public BooleanExpression searchByPrice(Integer price) {
        return price != null ? payment.price.goe(price) : null;
    }

    public BooleanExpression searchByBank(String bank) {
        return StringUtils.hasText(bank) ? payment.bank.contains(bank) : null;
    }

    public BooleanExpression searchByState(Payment.State state) {
        return state != null ? payment.state.eq(state) : null;
    }

    
}
