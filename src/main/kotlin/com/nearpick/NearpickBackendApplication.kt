package com.nearpick

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
class NearpickBackendApplication

fun main(args: Array<String>) {
    runApplication<NearpickBackendApplication>(*args)
}
