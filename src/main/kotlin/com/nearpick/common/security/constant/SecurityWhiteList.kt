package com.nearpick.common.security.constant

object SecurityWhiteList {

    /** Swagger - Public URL */
    val swagger: Array<String> = arrayOf(
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    )

    /** Public URL */
    val public: Array<String> = arrayOf(
        "/api/auth/login",
        "/api/auth/signup"
    )

    /** 전체 화이트리스트 */
    val all: Array<String> = swagger + public
}
