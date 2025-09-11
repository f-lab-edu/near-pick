package com.nearpick.app.domain.verification.repository

import com.nearpick.app.domain.verification.entity.VerificationEntity
import com.nearpick.app.domain.verification.enum.VerificationType
import org.springframework.data.jpa.repository.JpaRepository

interface VerificationRepository : JpaRepository<VerificationEntity, String> {
    fun findTopByTypeAndNameOrderByCreatedAtDesc(type: VerificationType, name: String): VerificationEntity?
}
