package com.nearpick.app.common.mail

import org.springframework.mail.MailException
import org.springframework.mail.MailSendException
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.net.SocketTimeoutException

@Component
class SmtpEmailSender(
    private val mailSender: JavaMailSender
) : EmailSender {
    override fun send(to: String, subject: String, content: String) {
        try {
            val message = SimpleMailMessage().apply {
                setTo(to)
                setSubject(subject)
                setText(content)
            }
            mailSender.send(message)
        } catch (e: SocketTimeoutException) {
            throw RuntimeException("메일 서버 연결이 지연되었습니다. 잠시 후 다시 시도해주세요.", e)
        } catch (e: MailSendException) {
            throw RuntimeException("메일 전송에 실패했습니다. 수신자 주소를 확인해주세요.", e)
        } catch (e: MailException) {
            throw RuntimeException("메일 전송 중 오류가 발생했습니다.", e)
        } catch (e: Exception) {
            throw RuntimeException("알 수 없는 오류로 메일을 전송할 수 없습니다.", e)
        }
    }
}
