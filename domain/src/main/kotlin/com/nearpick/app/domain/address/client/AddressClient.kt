package com.nearpick.app.domain.address.client

import com.nearpick.app.domain.address.dto.SearchAddressDto

interface AddressClient {
    fun searchAddress(query: String): Result<List<SearchAddressDto.Document>>
}
