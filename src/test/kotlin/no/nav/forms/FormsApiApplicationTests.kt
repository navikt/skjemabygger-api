package no.nav.forms

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FormsApiApplicationTests : ApplicationTest() {

	@Test
	fun applicationIsAliveAndReady() {
		assertEquals("OK", restTemplate.getForObject("/internal/health/isAlive", String::class.java))
		assertEquals("OK", restTemplate.getForObject("/internal/health/isReady", String::class.java))
	}

}
