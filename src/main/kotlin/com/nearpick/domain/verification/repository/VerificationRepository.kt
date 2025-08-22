package com.nearpick.domain.verification.repository

import com.nearpick.domain.verification.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository

interface VerificationRepository : JpaRepository<Verification, String> {
    fun findTopByTypeAndNameOrderByCreatedAtDesc(type: String, name: String): Verification?
}
