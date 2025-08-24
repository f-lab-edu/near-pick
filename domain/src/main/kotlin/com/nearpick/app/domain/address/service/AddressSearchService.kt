package com.nearpick.app.domain.address.service

import com.nearpick.app.domain.address.dto.SearchAddressResponse

interface AddressSearchService {
    fun searchAddress(query: String): List<SearchAddressResponse>?
}
