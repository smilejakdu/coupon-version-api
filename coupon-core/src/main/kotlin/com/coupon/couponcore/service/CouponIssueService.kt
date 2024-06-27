package com.coupon.couponcore.service

import com.coupon.couponcore.entities.Coupon
import com.coupon.couponcore.entities.CouponIssue
import com.coupon.couponcore.entities.event.CouponIssueCompleteEvent
import com.coupon.couponcore.exception.CouponIssueException
import com.coupon.couponcore.exception.ErrorCode
import com.coupon.couponcore.repository.mysql.CouponIssueJpaRepository
import com.coupon.couponcore.repository.mysql.CouponIssueRepository
import com.coupon.couponcore.repository.mysql.CouponJpaRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class CouponIssueService(
    private val couponJpaRepository: CouponJpaRepository,
    private val couponIssueJpaRepository: CouponIssueJpaRepository,
    private val couponIssueRepository: CouponIssueRepository,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    @Transactional
    fun issue(couponId: Long, userId: Long) {
        val coupon = findCouponWithLock(couponId)
        coupon.issue()
        saveCouponIssue(couponId, userId)
        publishCouponEvent(coupon)
    }

    @Transactional(readOnly = true)
    fun findCoupon(couponId: Long): Coupon {
        return couponJpaRepository.findById(couponId).orElseThrow {
            throw CouponIssueException(ErrorCode.COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. $couponId")
        }
    }

    @Transactional
    fun findCouponWithLock(couponId: Long): Coupon {
        return couponJpaRepository.findCouponWithLock(couponId).orElseThrow {
            throw CouponIssueException(ErrorCode.COUPON_NOT_EXIST, "쿠폰 정책이 존재하지 않습니다. $couponId")
        }
    }

    @Transactional
    fun saveCouponIssue(couponId: Long, userId: Long): CouponIssue {
        checkAlreadyIssuance(couponId, userId)
        val couponIssue = CouponIssue(
            couponId = couponId,
            userId = userId
        )
        return couponIssueJpaRepository.save(couponIssue)
    }

    private fun checkAlreadyIssuance(couponId: Long, userId: Long) {
        val issue = couponIssueRepository.findFirstCouponIssue(couponId, userId)
        if (issue != null) {
            throw CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "이미 발급된 쿠폰입니다. user_id: $userId, coupon_id: $couponId")
        }
    }

    private fun publishCouponEvent(coupon: Coupon) {
        if (coupon.isIssueComplete()) {
            applicationEventPublisher.publishEvent(CouponIssueCompleteEvent(coupon.id!!))
        }
    }
}