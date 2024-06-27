package com.coupon.couponcore.entities

import com.coupon.couponcore.exception.CouponIssueException
import com.coupon.couponcore.exception.ErrorCode
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "coupons")
class Coupon(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    val couponType: CouponType,

    val totalQuantity: Int? = null,

    @Column(nullable = false)
    var issuedQuantity: Int = 0,

    @Column(nullable = false)
    val discountAmount: Int,

    @Column(nullable = false)
    val minAvailableAmount: Int,

    @Column(nullable = false)
    val dateIssueStart: LocalDateTime,

    @Column(nullable = false)
    val dateIssueEnd: LocalDateTime
) : BaseTimeEntity() {

    fun availableIssueQuantity(): Boolean {
        return totalQuantity == null || totalQuantity > issuedQuantity
    }

    fun availableIssueDate(): Boolean {
        val now = LocalDateTime.now()
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now)
    }

    fun isIssueComplete(): Boolean {
        val now = LocalDateTime.now()
        return dateIssueEnd.isBefore(now) || !availableIssueQuantity()
    }

    fun issue() {
        if (!availableIssueQuantity()) {
            throw CouponIssueException(
                ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                "발급 가능한 수량을 초과합니다. total : $totalQuantity, issued: $issuedQuantity"
            )
        }
        if (!availableIssueDate()) {
            throw CouponIssueException(
                ErrorCode.INVALID_COUPON_ISSUE_DATE,
                "발급 가능한 일자가 아닙니다. request : ${LocalDateTime.now()}, issueStart: $dateIssueStart, issueEnd: $dateIssueEnd"
            )
        }
        issuedQuantity++
    }
}