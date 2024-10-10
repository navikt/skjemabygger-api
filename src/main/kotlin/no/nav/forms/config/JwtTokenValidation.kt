package no.nav.forms.config

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@EnableJwtTokenValidation(
	ignore = [
		"org.springframework",
		"io.swagger",
		"org.springdoc",
		"org.webjars.swagger-ui"
	]
)
@Profile("test | preprod | prod")
class JwtTokenValidation
