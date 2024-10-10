package no.nav.forms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class FormsApiApplication

fun main(args: Array<String>) {
	runApplication<FormsApiApplication>(*args)
}
