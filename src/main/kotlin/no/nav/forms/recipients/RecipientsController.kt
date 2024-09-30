package no.nav.forms.recipients

import no.nav.forms.api.RecipientsApi
import no.nav.forms.model.NewRecipientRequest
import no.nav.forms.model.RecipientDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipientsController(
	private val recipientsService: RecipientsService,
) : RecipientsApi {

	override fun getRecipients(): ResponseEntity<List<RecipientDto>> {
		return ResponseEntity.ok(recipientsService.getRecipients())
	}

	override fun createRecipient(newRecipientRequest: NewRecipientRequest): ResponseEntity<RecipientDto> {
		val userId = "testuser" // TODO user handler
		val dto = recipientsService.createRecipient(
			newRecipientRequest.recipientId,
			newRecipientRequest.name,
			newRecipientRequest.poBoxAddress,
			newRecipientRequest.postalCode,
			newRecipientRequest.postalName,
			newRecipientRequest.archiveSubjects,
			userId,
		)
		return ResponseEntity.ok(dto)
	}

}
