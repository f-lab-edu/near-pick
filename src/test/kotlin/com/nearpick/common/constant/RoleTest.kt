package com.nearpick.common.constant

import com.nearpick.common.exception.InvalidRoleException
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RoleTest : StringSpec({

    "대소문자 구분없이 ADMIN 제외 권한의 문자열로 들어오는 경우 권한 구분이 가능하므로 enum으로 변환된다" {
        Role.from("user") shouldBe Role.USER
        Role.from("SELLER") shouldBe Role.SELLER
        Role.from("UsEr") shouldBe Role.USER
    }

    //  ADMIN은 회원가입을 통한 생성이 불가능하고 직접 데이터를 넣는 방법만 가능
    // TODO: 추후 추가 개발 시 ADMIN 권한 계정을 생성하는 정책을 따로 설정할 예정
    "ADMIN 권한은 회원가입을 통해 생성이 불가능하므로 InvalidRoleException 발생한다" {
        listOf("ADMIN", "admin").forEach { item ->
            shouldThrowExactly<InvalidRoleException> {
                Role.from(item)
            }
        }
    }

    "null이나 빈 문자열, 존재하지 않는 권한의 문자열이 들어온 경우 권한을 확인할 수 없으므로 InvalidRoleException을 발생한다." {
        listOf("MANAGER", "GUEST", null, "").forEach { item ->
            shouldThrowExactly<InvalidRoleException> {
                Role.from(item)
            }
        }
    }
})
