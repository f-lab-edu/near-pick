package com.nearpick.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class NearpickBackendApplication

fun main(args: Array<String>) {
    runApplication<NearpickBackendApplication>(*args)
}
