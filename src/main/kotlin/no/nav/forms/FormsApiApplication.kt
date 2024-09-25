package no.nav.forms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FormsApiApplication

fun main(args: Array<String>) {
	runApplication<FormsApiApplication>(*args)
}
