package com.coupon.couponcore.repository.redis.dto

import com.coupon.couponcore.entities.Coupon
import com.coupon.couponcore.entities.CouponType
import com.coupon.couponcore.exception.CouponIssueException
import com.coupon.couponcore.exception.ErrorCode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import java.time.LocalDateTime

data class CouponRedisEntity(
    val id: Long,
    val couponType: CouponType,
    val totalQuantity: Int?,
    val availableIssueQuantity: Boolean,

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val dateIssueStart: LocalDateTime,

    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    val dateIssueEnd: LocalDateTime
) {

    constructor(coupon: Coupon) : this(
        id = coupon.id!!,
        couponType = coupon.couponType,
        totalQuantity = coupon.totalQuantity,
        availableIssueQuantity = coupon.availableIssueQuantity(),
        dateIssueStart = coupon.dateIssueStart,
        dateIssueEnd = coupon.dateIssueEnd
    )

    private fun availableIssueDate(): Boolean {
        val now = LocalDateTime.now()
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now)
    }

    fun checkIssuableCoupon() {
        if (!availableIssueQuantity) {
            throw CouponIssueException(
                ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                "모든 발급 수량이 소진되었습니다. coupon_id : $id"
            )
        }
        if (!availableIssueDate()) {
            throw CouponIssueException(
                ErrorCode.INVALID_COUPON_ISSUE_DATE,
                "발급 가능한 일자가 아닙니다. request : ${LocalDateTime.now()}, issueStart: $dateIssueStart, issueEnd: $dateIssueEnd"
            )
        }
    }
}