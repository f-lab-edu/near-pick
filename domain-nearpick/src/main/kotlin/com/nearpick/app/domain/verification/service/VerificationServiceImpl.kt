package com.nearpick.app.domain.verification.service

import com.nearpick.app.common.exception.InvalidEmailVerificationException
import com.nearpick.app.common.exception.InvalidEmailVerificationRequestException
import com.nearpick.app.common.exception.InvalidEmailVerificationTimeException
import com.nearpick.app.common.exception.InvalidEmailVerificationTokenException
import com.nearpick.app.common.mail.EmailSender
import com.nearpick.app.domain.verification.entity.Verification
import com.nearpick.app.domain.verification.enum.VerificationStatus
import com.nearpick.app.domain.verification.enum.VerificationType
import com.nearpick.app.domain.verification.repository.VerificationRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime


@Service
open class VerificationServiceImpl(
    private val repository: VerificationRepository,
    private val emailSender: EmailSender
) : VerificationService {

    private val EXPIRATION_MINUTES = 10L

    @Transactional
    override fun sendVerificationCode(type: VerificationType, email: String, subject: String) {
        val token = generateToken()

        val verification = Verification.from(type.name, email, token, EXPIRATION_MINUTES)
        repository.save(verification)

        val content = "인증 코드: $token"
        emailSender.send(email, subject, content)
    }

    @Transactional
    override fun verifyCode(type: VerificationType, email: String, token: String): Boolean {
        val verification = repository.findTopByTypeAndNameOrderByCreatedAtDesc(type.name, email)
            ?: throw InvalidEmailVerificationRequestException(email)

        return when {
            verification.status != VerificationStatus.PENDING.name -> {
                throw InvalidEmailVerificationException(email)
            }

            verification.validateDt.isBefore(LocalDateTime.now()) -> {
                verification.status = VerificationStatus.EXPIRED.name
                repository.save(verification)

                throw InvalidEmailVerificationTimeException(email)
            }

            verification.token != token -> {
                verification.status = VerificationStatus.FAILED.name
                repository.save(verification)

                throw InvalidEmailVerificationTokenException(email, token)
            }

            else -> {
                verification.status = VerificationStatus.VERIFIED.name
                repository.save(verification)

                true
            }
        }
    }

    @Transactional
    override fun isVerifyEmail(type: VerificationType, email: String): Boolean {
        val verification = repository.findTopByTypeAndNameOrderByCreatedAtDesc(type.name, email)
            ?: throw InvalidEmailVerificationRequestException(email)

        return when {
            verification.status != VerificationStatus.VERIFIED.name -> {
                throw InvalidEmailVerificationException(email)
            }

            verification.validateDt.isBefore(LocalDateTime.now()) -> {
                verification.status = VerificationStatus.EXPIRED.name
                repository.save(verification)

                throw InvalidEmailVerificationTimeException(email)
            }

            else -> {
                true
            }
        }
    }

    private fun generateToken(): String = (100_000..999_999).random().toString()
}

