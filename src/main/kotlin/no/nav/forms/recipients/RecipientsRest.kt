package no.nav.forms.recipients

import no.nav.forms.api.RecipientsApi
import no.nav.forms.model.Recipient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipientsRest : RecipientsApi {

	override fun getRecipients(): ResponseEntity<List<Recipient>> {
		return ResponseEntity.ok().body(emptyList())
	}

}
