package com.coupon.couponcore.exception

class CouponIssueException(
    val errorCode: ErrorCode,
    override val message: String
) : RuntimeException() {
    fun getMessage(): String {
        return "[${errorCode}] $message"
    }
}
