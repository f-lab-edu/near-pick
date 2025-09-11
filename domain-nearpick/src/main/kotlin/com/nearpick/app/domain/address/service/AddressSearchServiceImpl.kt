package com.nearpick.app.domain.address.service

import com.nearpick.app.common.exception.ExternalApiException
import com.nearpick.app.domain.address.client.KakaoAddressClient
import com.nearpick.app.domain.address.dto.SearchAddressResponse
import org.springframework.stereotype.Service

@Service
class AddressSearchServiceImpl(
    private val kakaoAddressClient: KakaoAddressClient
) : AddressSearchService {
    override fun searchAddress(query: String): List<SearchAddressResponse>? {
        val kakaoResults = kakaoAddressClient.searchAddress(query)
        kakaoResults.onSuccess {
            return it.map {
                SearchAddressResponse(
                    fullAddress = it.address?.address_name ?: it.road_address?.address_name,
                    province = it.address?.region_1depth_name,
                    district = it.address?.region_2depth_name,
                    neighborhood = it.address?.region_3depth_name,
                    street = it.road_address?.road_name,
                    buildingNumber = it.road_address?.main_building_no
                )
            }
        }.onFailure { exception -> throw ExternalApiException(exception.message) }
        return null
    }
}
