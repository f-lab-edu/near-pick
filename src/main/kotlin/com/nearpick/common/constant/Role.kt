package com.nearpick.common.constant

import com.nearpick.common.exception.InvalidRoleException

enum class Role {
    USER, SELLER, ADMIN;

    companion object {
        fun from(value: String?): Role {
            val upper = value?.uppercase() ?: USER.name

            if (upper == ADMIN.name) throw InvalidRoleException(value)

            return try {
                Role.valueOf(upper)
            } catch (e: IllegalArgumentException) {
                throw InvalidRoleException(value)
            }
        }
    }
}
