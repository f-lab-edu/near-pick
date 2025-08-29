package com.nearpick.app.common.validator

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ValidatorTest : StringSpec({

    //isValidBusinessRegistrationNumber
    "사업자등록번호는 10개의 숫자로 되어 있다. (특수문자 제외)" {
        listOf("6789", "0-123").forEach { item ->
            Validator.isValidBusinessRegistrationNumber(item) shouldBe false
        }
    }

    "사업자등록번호의 (앞 9개 * 가중치의 합) + (9번째 값*5 / 10)의 일의 자리 값이 10번째 자리의 값이 같으면 유효하다." {
        listOf("100-00-00009", "100-00-00014").forEach{ item ->
            Validator.isValidBusinessRegistrationNumber(item) shouldBe true
        }

        listOf("100-00-00008", "123-45-67890").forEach{ item ->
            Validator.isValidBusinessRegistrationNumber(item) shouldBe false
        }
    }

})
