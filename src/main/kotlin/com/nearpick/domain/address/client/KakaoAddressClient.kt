package com.nearpick.domain.address.client

import com.nearpick.domain.address.dto.SearchAddressDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Component
class KakaoAddressClient(
    @Value("\${kakao.rest-api-key}") private val kakaoApiKey: String,
    private val webClientBuilder: WebClient.Builder
) {
    private val kakaoBaseUrl = "https://dapi.kakao.com"

    fun searchAddress(query: String): Result<List<SearchAddressDto.Document>> {
        return runCatching {
            webClientBuilder.build()
                .get()
                .uri("$kakaoBaseUrl/v2/local/search/address.json?query={query}", query)
                .header("Authorization", "KakaoAK $kakaoApiKey")
                .retrieve()
                .bodyToMono(SearchAddressDto::class.java)
                .timeout(Duration.ofSeconds(3))
                .block()
                ?.documents ?: emptyList()
        }
    }
}
