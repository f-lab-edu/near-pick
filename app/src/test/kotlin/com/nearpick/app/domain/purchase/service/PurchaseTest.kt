package com.nearpick.app.domain.purchase.service

import com.nearpick.app.common.constant.Role
import com.nearpick.app.common.exception.PurchaseStatusInvalidRoleException
import com.nearpick.app.common.exception.PurchaseStatusInvalidTransitionException
import com.nearpick.app.domain.product.enum.ProductType
import com.nearpick.app.domain.purchase.dto.CreatePurchaseRequest
import com.nearpick.app.domain.purchase.dto.UpdatePurchaseRequest
import com.nearpick.app.domain.purchase.enum.PurchaseStatus
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.UUID

class PurchaseTest : BehaviorSpec({
    val userId = UUID.randomUUID().toString()

    val baseRequest = CreatePurchaseRequest(
        quantity = 2,
        reservationDt = LocalDateTime.of(2025, 10, 22, 15, 0),
        requestMessage = "테스트 요청 메시지",
        productId = "test UUID",
        price = BigInteger.valueOf(10000),
        productType = ProductType.FIRST_COME
    )

    fun newPurchase(): Purchase {
        val purchase = Purchase.create(
            userId = userId,
            productId = baseRequest.productId,
            productType = baseRequest.productType,
            price = baseRequest.price,
            quantity = baseRequest.quantity,
            reservationDt = baseRequest.reservationDt,
            message = baseRequest.requestMessage
        )
        return purchase
    }

    Given("create()를 호출할 때 요청한 데이터를 기준으로 저장할 데이터를 생성한다.") {
        When("정상적인 요청이면") {
            val purchase = newPurchase()

            Then("status는 PENDING 상태로 생성되어야 한다") {
                purchase.status shouldBe PurchaseStatus.PENDING
            }

            Then("총 금액은 product.price * quantity로 계산되어야 한다") {
                purchase.totalPrice shouldBe BigInteger("20000")
            }

            Then("UUID가 자동 생성되어야 한다") {
                purchase.id shouldNotBe null
            }

            Then("요청 메시지와 예약일이 올바르게 설정된다") {
                purchase.requestMessage shouldBe baseRequest.requestMessage
                purchase.reservationDt shouldBe baseRequest.reservationDt
            }
        }
    }

    Given("update()를 호출할 때 ") {
        val purchase = newPurchase()

        val updateRequest = UpdatePurchaseRequest(
            price = BigInteger("30000"),
            quantity = 3,
            reservationDt = LocalDateTime.of(2025, 10, 30, 12, 0),
            requestMessage = "변경된 요청 메시지"
        )

        When("값을 변경하면") {
            purchase.update(
                price = updateRequest.price,
                quantity = updateRequest.quantity,
                reservationDt = updateRequest.reservationDt,
                message = updateRequest.requestMessage
            )

            Then("quantity, price, reservationDt, requestMessage가 모두 변경된다") {
                purchase.quantity shouldBe 3
                purchase.totalPrice shouldBe BigInteger("30000")
                purchase.reservationDt shouldBe updateRequest.reservationDt
                purchase.requestMessage shouldBe updateRequest.requestMessage
            }
        }
    }

    Given("updateStatus()를 통해 USER와 SELLER가 상태 변경을 진행한다.") {
        When("USER가 CANCELLED로 상태 변경 시도할 때") {
            val purchase = newPurchase()
            Then("허용된 상태이므로 성공한다") {
                purchase.updateStatus(Role.USER, PurchaseStatus.CANCELLED)
                purchase.status shouldBe PurchaseStatus.CANCELLED
            }
        }

        When("USER가 CONFIRMED로 변경하려고 하면") {
            val purchase = newPurchase()
            Then("SELLER가 입금 확인 후 변경하는 상태값이므로 PurchaseStatusInvalidRoleException이 발생한다") {
                shouldThrow<PurchaseStatusInvalidRoleException> {
                    purchase.updateStatus(Role.USER, PurchaseStatus.CONFIRMED)
                }
            }
        }

        When("USER가 CANCELLED 이후 CONFIRMED로 재요청하면") {
            val purchase = newPurchase()
            purchase.updateStatus(Role.USER, PurchaseStatus.CANCELLED)

            Then("취소한 주문의 상태를 다시 변경할 수 없으므로 PurchaseStatusInvalidRoleException이 발생한다") {
                shouldThrow<PurchaseStatusInvalidRoleException> {
                    purchase.updateStatus(Role.USER, PurchaseStatus.CONFIRMED)
                }
            }
        }

        When("SELLER가 CONFIRMED 상태로 변경하려고 하면") {
            val purchase = newPurchase()
            Then("성공해야 한다") {
                purchase.updateStatus(Role.SELLER, PurchaseStatus.CONFIRMED)
                purchase.status shouldBe PurchaseStatus.CONFIRMED
            }
        }

        When("SELLER가 CONFIRMED → SUCCESS로 상태 변경하면") {
            val purchase = newPurchase()
            purchase.updateStatus(Role.SELLER, PurchaseStatus.CONFIRMED)
            Then("입금 대기 확인 후이므로 성공한다") {
                purchase.updateStatus(Role.SELLER, PurchaseStatus.SUCCESS)
                purchase.status shouldBe PurchaseStatus.SUCCESS
            }
        }

        When("SELLER가 입금 확인 상태인 CONFIRMED을 거치지 않고 PENDING -> SUCCESS 으로 바로 변경하려고 하면") {
            val purchase = newPurchase()
            Then("입금이 확인되지 않았으므로 PurchaseStatusInvalidTransitionException이 발생한다") {
                shouldThrow<PurchaseStatusInvalidTransitionException> {
                    purchase.updateStatus(Role.SELLER, PurchaseStatus.SUCCESS)
                }
            }
        }

        When("SELLER가 주문 입금 확인을 잘못해서 CONFIRMED 상태에서 PENDING으로 돌아가려고 하면") {
            val purchase = newPurchase()
            purchase.updateStatus(Role.SELLER, PurchaseStatus.CONFIRMED)

            Then("성공한다.") {
                purchase.updateStatus(Role.SELLER, PurchaseStatus.PENDING)
                purchase.status shouldBe PurchaseStatus.PENDING
            }
        }

        When("이미 취소된 상태인 CANCELLED에서 SUCCESS로 변경하려고 하면") {
            val purchase = newPurchase()
            purchase.updateStatus(Role.USER, PurchaseStatus.CANCELLED)

            Then("이미 취소된 주문은 되돌릴 수 없으므로 PurchaseStatusInvalidTransitionException이 발생한다") {
                shouldThrow<PurchaseStatusInvalidTransitionException> {
                    purchase.updateStatus(Role.SELLER, PurchaseStatus.SUCCESS)
                }
            }
        }

        When("SUCCESS 상태에서 CANCELLED로 변경하려고 하면") {
            val purchase = newPurchase()
            purchase.updateStatus(Role.SELLER, PurchaseStatus.CONFIRMED)
            purchase.updateStatus(Role.SELLER, PurchaseStatus.SUCCESS)

            Then("이미 완료된 주문이므로 PurchaseStatusInvalidTransitionException이 발생한다.") {
                shouldThrow<PurchaseStatusInvalidTransitionException> {
                    purchase.updateStatus(Role.SELLER, PurchaseStatus.CANCELLED)
                }
            }
        }
    }
})
