package com.eureka.spartaonetoone.order.infrastructure;

import static com.querydsl.core.types.Order.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.eureka.spartaonetoone.order.domain.Order;
import com.eureka.spartaonetoone.order.domain.QOrder;
import com.eureka.spartaonetoone.order.domain.repository.CustomOrderRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements CustomOrderRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Order> searchOrders(String userRole, UUID storeId, UUID userId, Pageable pageable) {
		QOrder order = QOrder.order;

		BooleanBuilder whereClause = new BooleanBuilder();
		if (storeId != null) {
			whereClause.and(order.storeId.eq(storeId));
		}
		if (userId != null) {
			whereClause.and(order.userId.eq(userId));
		}
		if (!userRole.equals("ROLE_ADMIN")) {
			whereClause.and(order.isDeleted.eq(false));
		}

		JPAQuery<Order> query = queryFactory.selectFrom(QOrder.order)
			.from(order)
			.where(whereClause)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		for (Sort.Order sortOrder : pageable.getSort()) {
			switch (sortOrder.getProperty()) {
				case "createdAt" ->
					query.orderBy(sortOrder.isAscending() ? order.createdAt.asc() : order.createdAt.desc());
				case "updatedAt" ->
					query.orderBy(sortOrder.isAscending() ? order.updatedAt.asc() : order.updatedAt.desc());
			}
		}

		List<Order> orders = query.fetch();

		return new PageImpl<>(orders, pageable, orders.size());
	}
}
