package com.coupon.couponcore.service

import com.coupon.couponcore.exception.CouponIssueException
import com.coupon.couponcore.exception.ErrorCode
import com.coupon.couponcore.repository.redis.RedisRepository
import com.coupon.couponcore.repository.redis.dto.CouponRedisEntity
import jakarta.persistence.Id
import org.springframework.stereotype.Service


@Service
class CouponIssueRedisService(
    private val redisRepository: RedisRepository
) {

    fun checkCouponIssueQuantity(coupon: CouponRedisEntity, userId: Long) {
        if (!availableUserIssueQuantity(coupon.id, userId)) {
            throw CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "발급 가능한 수량을 초과합니다. couponId : ${coupon.id}, userId: $userId")
        }
        if (!availableTotalIssueQuantity(coupon.totalQuantity, coupon.id)) {
            throw CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY, "발급 가능한 수량을 초과합니다. couponId : ${coupon.id}, userId : $userId")
        }
    }

    fun availableTotalIssueQuantity(totalQuantity: Int?, couponId: Long): Boolean {
        if (totalQuantity == null) {
            return true
        }
        val key = getIssueRequestKey(couponId)
        return totalQuantity > redisRepository.sCard(key)!!
    }

    fun availableUserIssueQuantity(couponId: Long, userId: Long): Boolean {
        val key = getIssueRequestKey(couponId)
        return redisRepository.sIsMember(key, userId.toString())?.let { !it } ?: true
    }

    fun getIssueRequestKey(couponId: Long): String {
        return "issue_request_key:$couponId"
    }
}