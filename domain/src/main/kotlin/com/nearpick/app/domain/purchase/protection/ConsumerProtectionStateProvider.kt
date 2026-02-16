package com.nearpick.app.domain.purchase.protection

interface ConsumerProtectionStateProvider {
    fun getState(): ConsumerProtectionState
}
