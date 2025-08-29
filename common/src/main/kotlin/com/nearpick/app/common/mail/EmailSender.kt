package com.nearpick.app.common.mail

interface EmailSender {
    fun send(to: String, subject: String, content: String)
}
