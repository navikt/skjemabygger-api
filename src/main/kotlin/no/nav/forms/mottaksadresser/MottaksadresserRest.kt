package no.nav.forms.mottaksadresser

import no.nav.forms.api.MottaksadresserApi
import no.nav.forms.model.Mottaksadresse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class MottaksadresserRest : MottaksadresserApi {

	override fun getMottaksadresser(): ResponseEntity<List<Mottaksadresse>> {
		return ResponseEntity.ok().body(emptyList())
	}

}
