package com.nearpick.domain.address.controller

import com.nearpick.domain.address.dto.SearchAddressResponse
import com.nearpick.domain.address.service.AddressSearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/address")
class AddressSearchController(
    private val addressSearchService: AddressSearchService
) {
    @Operation(summary = "주소 검색", description = "검색어로 주소를 검색합니다.")
    @ApiResponse(responseCode = "200", description = "주소 검색 성공")
    @GetMapping("/search")
    fun searchAddress(@RequestParam query: String): List<SearchAddressResponse>? {
        return addressSearchService.searchAddress(query)
    }
}
