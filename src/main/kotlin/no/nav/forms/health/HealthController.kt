package no.nav.forms.health

import no.nav.forms.api.HealthApi
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@Unprotected
@RestController
class HealthController : HealthApi {

	override fun isAlive(): ResponseEntity<String> = ResponseEntity.ok("OK")

	override fun isReady(): ResponseEntity<String> = ResponseEntity.ok("OK")

}
