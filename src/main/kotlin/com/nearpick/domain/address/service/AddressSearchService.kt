package com.nearpick.domain.address.service

import com.nearpick.domain.address.client.KakaoAddressClient
import com.nearpick.domain.address.dto.SearchAddressResponse
import com.nearpick.domain.address.mapper.toDtoList
import org.springframework.stereotype.Service

@Service
class AddressSearchService(
    private val kakaoAddressClient: KakaoAddressClient
) {
    fun searchAddress(query: String): List<SearchAddressResponse>? {
        val kakaoResults = kakaoAddressClient.searchAddress(query)
        kakaoResults.onSuccess {
            return it.toDtoList()
        }.onFailure { exception -> throw exception }
        return null
    }
}
