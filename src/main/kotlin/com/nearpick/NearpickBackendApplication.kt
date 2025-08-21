package com.nearpick

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// @EnableJpaAuditing
@SpringBootApplication
class NearpickBackendApplication

fun main(args: Array<String>) {
    runApplication<NearpickBackendApplication>(*args)
}
