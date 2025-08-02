package com.nearpick.domain.address.client

import com.nearpick.domain.address.dto.SearchAddressDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class KakaoAddressClient(
    @Value("\${kakao.rest-api-key}") private val kakaoApiKey: String,
    private val webClientBuilder: WebClient.Builder
) {
    private val kakaoBaseUrl = "https://dapi.kakao.com"

    fun searchAddress(query: String): List<SearchAddressDto.Document> {
        return webClientBuilder.build()
            .get()
            .uri("$kakaoBaseUrl/v2/local/search/address.json?query={query}", query)
            .header("Authorization", "KakaoAK $kakaoApiKey")
            .retrieve()
            .bodyToMono(SearchAddressDto::class.java)
            .block()
            ?.documents ?: emptyList()
    }
}
