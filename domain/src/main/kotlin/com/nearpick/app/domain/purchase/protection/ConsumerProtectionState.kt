package com.nearpick.app.domain.purchase.protection

enum class ConsumerProtectionState(val code: Int) {
    NORMAL(0),
    THROTTLED(1),
    PAUSED(2)
}
