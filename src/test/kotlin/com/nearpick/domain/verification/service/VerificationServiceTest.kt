package com.nearpick.domain.verification.service

import com.nearpick.common.exception.InvalidEmailVerificationException
import com.nearpick.common.exception.InvalidEmailVerificationTimeException
import com.nearpick.common.exception.InvalidEmailVerificationTokenException
import com.nearpick.common.mail.EmailSender
import com.nearpick.domain.verification.entity.Verification
import com.nearpick.domain.verification.enum.VerificationStatus
import com.nearpick.domain.verification.enum.VerificationType
import com.nearpick.domain.verification.repository.VerificationRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*

class VerificationServiceTest : StringSpec({
    val verificationRepository = mock<VerificationRepository>()
    val emailSender = mock<EmailSender>()
    val verificationService = VerificationService(verificationRepository, emailSender)

    "만료 시간이 지난 경우 InvalidEmailVerificationTimeException을 발생한다." {
        val email = "user@example.com"
        val expired = Verification.from(VerificationType.SIGNUP_EMAIL.name, email, "123456", -10L)

        `when`(
            verificationRepository.findTopByTypeAndNameOrderByCreatedAtDesc(VerificationType.SIGNUP_EMAIL.name, email)
        ).thenReturn(expired)

        `when`(verificationRepository.save(any(Verification::class.java))).thenAnswer { it.arguments[0] }

        shouldThrow<InvalidEmailVerificationTimeException> {
            verificationService.verifyCode(VerificationType.SIGNUP_EMAIL, email, expired.token)
        }
        expired.status shouldBe VerificationStatus.EXPIRED.name
    }

    "PENDING으로 저장되어있지 않은 경우 이미 검증이 완료된 상태이므로 InvalidEmailVerificationException을 발생한다." {
        val email = "user@example.com"
        val verified = Verification(
            id = UUID.randomUUID().toString(),
            type = VerificationType.SIGNUP_EMAIL.name,
            name = email,
            token = "123456",
            status = VerificationStatus.VERIFIED.name,
            validateDt = LocalDateTime.now().plusMinutes(10L)
        )

        `when`(
            verificationRepository.findTopByTypeAndNameOrderByCreatedAtDesc(VerificationType.SIGNUP_EMAIL.name, email)
        ).thenReturn(verified)

        shouldThrow<InvalidEmailVerificationException> {
            verificationService.verifyCode(VerificationType.SIGNUP_EMAIL, email, verified.token)
        }
    }

    "토큰 값이 틀린 경우 InvalidEmailVerificationTokenException을 발생한다" {
        val email = "user@example.com"
        val pending = Verification.from(VerificationType.SIGNUP_EMAIL.name, email, "654321", 10L)

        `when`(
            verificationRepository.findTopByTypeAndNameOrderByCreatedAtDesc(VerificationType.SIGNUP_EMAIL.name, email)
        ).thenReturn(pending)

        `when`(verificationRepository.save(any(Verification::class.java))).thenAnswer { it.arguments[0] }

        shouldThrow<InvalidEmailVerificationTokenException> {
            verificationService.verifyCode(VerificationType.SIGNUP_EMAIL, email, "123456")
        }

        pending.status shouldBe VerificationStatus.FAILED.name
    }

    "토큰과 인증 정보가 모두 유효한 경우 true를 반환한다." {
        val email = "user@example.com"
        val ok = Verification.from(VerificationType.SIGNUP_EMAIL.name, email, "123456", 10L)

        `when`(
            verificationRepository.findTopByTypeAndNameOrderByCreatedAtDesc(VerificationType.SIGNUP_EMAIL.name, email)
        ).thenReturn(ok)

        `when`(verificationRepository.save(any(Verification::class.java))).thenAnswer { it.arguments[0] }

        val result = verificationService.verifyCode(VerificationType.SIGNUP_EMAIL, email, "123456")
        result shouldBe true
        ok.status shouldBe VerificationStatus.VERIFIED.name
        verify(verificationRepository, atLeastOnce()).save(any(Verification::class.java))
    }

    "회원가입, 이메일 수정을 위해 인증 정보를 확인할 때 인증 정보가 유효한 상태가 아닐 경우 InvalidEmailVerificationException을 발생한다." {
        val email = "user@example.com"
        val pending = Verification.from(VerificationType.SIGNUP_EMAIL.name, email, "123456", 10L)

        `when`(
            verificationRepository.findTopByTypeAndNameOrderByCreatedAtDesc(VerificationType.SIGNUP_EMAIL.name, email)
        ).thenReturn(pending)

        shouldThrow<InvalidEmailVerificationException> {
            verificationService.isVerifyEmail(VerificationType.SIGNUP_EMAIL, email)
        }
    }

    "회원가입, 이메일 수정을 위해 인증 정보를 확인할 때 인증 정보가 유효해도 만료 시간이 지난 경우 InvalidEmailVerificationTimeException을 발생한다." {
        val email = "user@example.com"
        val late = Verification(
            id = UUID.randomUUID().toString(),
            type = VerificationType.SIGNUP_EMAIL.name,
            name = email,
            token = "123456",
            status = VerificationStatus.VERIFIED.name,
            validateDt = LocalDateTime.now().minusSeconds(1)
        )

        `when`(
            verificationRepository.findTopByTypeAndNameOrderByCreatedAtDesc(VerificationType.SIGNUP_EMAIL.name, email)
        ).thenReturn(late)

        shouldThrow<InvalidEmailVerificationTimeException> {
            verificationService.isVerifyEmail(VerificationType.SIGNUP_EMAIL, email)
        }
    }
})
