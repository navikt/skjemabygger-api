package no.nav.forms.security

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

interface TokenHandler {
	fun getUserIdFromToken(): String
}

@Component
@Profile("test | local")
class TokenHandlerTestImpl : TokenHandler {
	override fun getUserIdFromToken(): String {
		return "testuser"
	}
}
