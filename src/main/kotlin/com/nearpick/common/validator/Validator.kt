package com.nearpick.common.validator

object Validator {
    fun isValidPhoneNumber(phone: String): Boolean {
        return phone.length == 11
    }

    fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}\$")
        return regex.matches(email)
    }
}
