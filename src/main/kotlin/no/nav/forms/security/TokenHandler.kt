package no.nav.forms.security

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

interface TokenHandler {
	fun getUserIdFromToken(): String
}

@Component
@Profile("preprod | prod")
class TokenHandlerImpl : TokenHandler {
	override fun getUserIdFromToken(): String {
		throw NotImplementedError("TokenHandler::getUserIdFromToken() is not implemented yet")
	}
}

@Component
@Profile("test | local | docker")
class TokenHandlerTestImpl : TokenHandler {
	override fun getUserIdFromToken(): String {
		return "testuser"
	}
}
