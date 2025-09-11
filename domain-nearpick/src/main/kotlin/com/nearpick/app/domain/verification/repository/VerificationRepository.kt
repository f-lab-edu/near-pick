package com.nearpick.app.domain.verification.repository

import com.nearpick.app.domain.verification.entity.VerificationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VerificationRepository : JpaRepository<VerificationEntity, String> {
    fun findTopByTypeAndNameOrderByCreatedAtDesc(type: String, name: String): VerificationEntity?
}
