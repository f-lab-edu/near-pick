package com.nearpick.domain.address.dto

class SearchAddressResponse(
    val documents: List<Document>
) {
    data class Document(
        val address: AddressDetail?, // 지번
        val road_address: RoadAddressDetail?, // 도로명
        val x: String?, // 경도 (longitude)
        val y: String? // 위도 (latitude)
    )

    data class AddressDetail(
        val address_name: String,
        val region_1depth_name: String,
        val region_2depth_name: String,
        val region_3depth_name: String
    )

    data class RoadAddressDetail(
        val address_name: String, // 전체 도로명 주소
        val road_name: String, // 도로명
        val main_building_no: String // 건물 번호
    )
}
