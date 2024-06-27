package com.coupon.couponcore.repository.redis.dto

data class CouponIssueRequest(
    val couponId: Long,
    val userId: Long
)