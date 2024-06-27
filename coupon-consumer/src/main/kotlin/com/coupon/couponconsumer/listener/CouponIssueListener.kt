package com.coupon.couponconsumer.listener

import com.coupon.couponcore.repository.redis.RedisRepository
import com.coupon.couponcore.repository.redis.dto.CouponIssueRequest
import com.coupon.couponcore.service.CouponIssueService
import com.coupon.couponcore.utils.logger
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@EnableScheduling
@Component
class CouponIssueListener(
    private val couponIssueService: CouponIssueService,
    private val redisRepository: RedisRepository
) {

    private val objectMapper = ObjectMapper()
    private val issueRequestQueueKey = getIssueRequestQueueKey()
    val log = logger()

    @Scheduled(fixedDelay = 1000)
    fun issue() {
        log.info("listen...")
        while (existCouponIssueTarget()) {
            try {
                val target = getIssueTarget()
                log.info("발급 시작 target: $target")
                couponIssueService.issue(target.couponId, target.userId)
                log.info("발급 완료 target: $target")
                removeIssuedTarget()
            } catch (e: JsonProcessingException) {
                log.error("Error processing JSON", e)
            }
        }
    }

    private fun existCouponIssueTarget(): Boolean {
        return redisRepository.lSize(issueRequestQueueKey)?.let { it > 0 } ?: false
    }

    @Throws(JsonProcessingException::class)
    private fun getIssueTarget(): CouponIssueRequest {
        return objectMapper.readValue(redisRepository.lIndex(issueRequestQueueKey, 0), CouponIssueRequest::class.java)
    }

    private fun removeIssuedTarget() {
        redisRepository.lPop(issueRequestQueueKey)
    }

    private fun getIssueRequestQueueKey(): String {
        return "someQueueKey"
    }
}