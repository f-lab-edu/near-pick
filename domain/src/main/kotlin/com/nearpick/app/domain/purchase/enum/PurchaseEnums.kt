package com.nearpick.app.domain.purchase.enum

enum class PurchaseStatus(
    val description: String
) {
    PENDING("결제 대기"),
    CONFIRMED("결제 완료"),
    CANCELLED("주문 취소"),
    SUCCESS("구매 완료");
}

enum class PurchaseCancelReason(
    val description: String
) {
    STORE_ISSUE("판매자 사유"),
    USER_REQUEST("구매자 요청"),
    OUT_OF_STOCK("재고 부족"),
    NO_SHOW("미방문"),
    PAYMENT_TIMEOUT("결제 지연");
}
