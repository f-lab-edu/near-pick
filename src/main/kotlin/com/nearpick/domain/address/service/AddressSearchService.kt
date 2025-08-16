package com.nearpick.domain.address.service

import com.nearpick.common.exception.ExternalApiException
import com.nearpick.domain.address.client.KakaoAddressClient
import com.nearpick.domain.address.dto.SearchAddressResponse
import org.springframework.stereotype.Service

@Service
class AddressSearchService(
    private val kakaoAddressClient: KakaoAddressClient
) {
    fun searchAddress(query: String): List<SearchAddressResponse>? {
        val kakaoResults = kakaoAddressClient.searchAddress(query)
        kakaoResults.onSuccess {
            return SearchAddressResponse.toDtoList(it)
        }.onFailure { exception -> throw ExternalApiException(exception.message) }
        return null
    }
}
