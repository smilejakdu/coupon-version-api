package com.coupon.couponcore.repository.mysql

import com.coupon.couponcore.entities.CouponIssue
import org.springframework.data.jpa.repository.JpaRepository

interface CouponIssueJpaRepository: JpaRepository<CouponIssue, Long> {
}