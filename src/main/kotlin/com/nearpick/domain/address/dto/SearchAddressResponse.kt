package com.nearpick.domain.address.dto

data class SearchAddressResponse(
    val fullAddress: String?,

    val province: String?,
    val district: String?,
    val neighborhood: String?,
    val street: String?,
    val buildingNumber: String?
) {
    companion object {
        fun toDtoList(searchAddressList: List<SearchAddressDto.Document>): List<SearchAddressResponse> {
            return searchAddressList.map {
                SearchAddressResponse(
                    fullAddress = it.address?.address_name ?: it.road_address?.address_name,
                    province = it.address?.region_1depth_name,
                    district = it.address?.region_2depth_name,
                    neighborhood = it.address?.region_3depth_name,
                    street = it.road_address?.road_name,
                    buildingNumber = it.road_address?.main_building_no
                )
            }
        }
    }
}
