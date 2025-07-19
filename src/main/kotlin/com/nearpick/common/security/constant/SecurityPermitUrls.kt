package com.nearpick.common.security.constant

object SecurityPermitUrls {

    /** Swagger - Public URL */
    val SWAGGER_URLS: Array<String> = arrayOf(
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    )

    /** Public URL */
    val PUBLIC_URLS: Array<String> = arrayOf(
        "/api/auth/login",
        "/api/auth/logout",
        "/api/auth/signup",
        "/api/users"
    )

    /** 전체 Public URL */
    val ALL_PUBLIC_URLS: Array<String> = SWAGGER_URLS + PUBLIC_URLS

    /** 사용자 접근 가능 URL */
    val USER_URLS = listOf(
        "/api/purchase" // TODO: 임시 URL, 이후 user 관련 URL 나오면 수정 예정
    ).toTypedArray()

    /** SELLER 접근 가능 URL */
    val SELLER_URLS = listOf(
        "/api/products/**" // TODO: 임시 URL, 이후 seller 관련 URL 나오면 수정 예정
    ).toTypedArray()

    /** ADMIN 접근 가능 URL */
    val ADMIN_URLS = listOf(
        "/api/admin/**" // TODO: 임시 URL, 이후 admin 관련 URL 나오면 수정 예정
    ).toTypedArray()
}
