package no.nav.forms.recipients

import no.nav.forms.api.RecipientsApi
import no.nav.forms.model.NewRecipientRequest
import no.nav.forms.model.RecipientDto
import no.nav.forms.model.UpdateRecipientRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class RecipientsController(
	private val recipientsService: RecipientsService,
) : RecipientsApi {

	override fun getRecipients(): ResponseEntity<List<RecipientDto>> {
		return ResponseEntity.ok(recipientsService.getRecipients())
	}

	override fun getRecipient(recipientId: String): ResponseEntity<RecipientDto> {
		return ResponseEntity.ok(recipientsService.getRecipient(recipientId))
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

	override fun updateRecipient(recipientId: String, updateRecipientRequest: UpdateRecipientRequest): ResponseEntity<RecipientDto> {
		val userId = "testuser" // TODO user handler
		val dto = recipientsService.updateRecipient(
			recipientId,
			updateRecipientRequest.name,
			updateRecipientRequest.poBoxAddress,
			updateRecipientRequest.postalCode,
			updateRecipientRequest.postalName,
			updateRecipientRequest.archiveSubjects,
			userId,
		)
		return ResponseEntity.ok(dto)
	}

}
