package com.coupon.couponcore.entities

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
class CouponIssue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var couponId: Long,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false)
    @CreatedDate
    var dateIssued: LocalDateTime? = null,

    var dateUsed: LocalDateTime? = null
) : BaseTimeEntity()
