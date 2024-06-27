package com.coupon.couponcore.repository.mysql

import com.coupon.couponcore.entities.CouponIssue
import com.querydsl.jpa.JPQLQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CouponIssueRepository(
    private val queryFactory: JPQLQueryFactory
) {

    fun findFirstCouponIssue(couponId: Long, userId: Long): CouponIssue? {
        return queryFactory.selectFrom(couponIssue)
            .where(couponIssue.couponId.eq(couponId))
            .where(couponIssue.userId.eq(userId))
            .fetchFirst()
    }
}