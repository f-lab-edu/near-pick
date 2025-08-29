package com.nearpick.app.domain.verification.repository

import com.nearpick.app.domain.verification.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository

interface VerificationRepository : JpaRepository<Verification, String> {
    fun findTopByTypeAndNameOrderByCreatedAtDesc(type: String, name: String): Verification?
}
