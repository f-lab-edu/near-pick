package com.nearpick.common.validator

object Validator {
    fun isValidPhoneNumber(phone: String): Boolean {
        return phone.length == 11
    }

    fun isValidNickname(nickname: String): Boolean {
        return nickname.length <= 15
    }

    fun isValidEmail(email: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}\$")
        return regex.matches(email)
    }

    fun isValidBusinessRegistrationNumber(number: String): Boolean {
        val digits = number.filter { it.isDigit() }
        if (digits.length != 10) return false

        val weights = listOf(1, 3, 7, 1, 3, 7, 1, 3, 5)

        val sum = (0 until 9).sumOf { i ->
            (digits[i].toString().toInt() * weights[i])
        } + ((digits[8].toString().toInt() * 5) / 10)

        val checkDigit = (10 - (sum % 10)) % 10
        return digits[9].toString().toInt() == checkDigit
    }
}
