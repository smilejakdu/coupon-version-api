package com.coupon.couponcore.repository.redis

import com.coupon.couponcore.exception.CouponIssueException
import com.coupon.couponcore.exception.ErrorCode

enum class CouponIssueRequestCode(val code: Int) {
    SUCCESS(1),
    DUPLICATED_COUPON_ISSUE(2),
    INVALID_COUPON_ISSUE_QUANTITY(3);

    companion object {
        fun find(code: String): CouponIssueRequestCode {
            val codeValue = code.toInt()
            return values().find { it.code == codeValue }
                ?: throw IllegalArgumentException("존재하지 않는 코드입니다. $code")
        }

        fun checkRequestResult(code: CouponIssueRequestCode) {
            when (code) {
                INVALID_COUPON_ISSUE_QUANTITY -> {
                    throw CouponIssueException(
                        ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                        "발급 가능한 수량을 초과합니다"
                    )
                }
                DUPLICATED_COUPON_ISSUE -> {
                    throw CouponIssueException(
                        ErrorCode.DUPLICATED_COUPON_ISSUE,
                        "이미 발급된 쿠폰입니다."
                    )
                }
                else -> {}
            }
        }
    }
}