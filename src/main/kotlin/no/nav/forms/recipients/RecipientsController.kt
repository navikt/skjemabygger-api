package no.nav.forms.recipients

import no.nav.forms.api.RecipientsApi
import no.nav.forms.config.AzureAdConfig
import no.nav.forms.model.NewRecipientRequest
import no.nav.forms.model.RecipientDto
import no.nav.forms.model.UpdateRecipientRequest
import no.nav.forms.security.SecurityContextHolder
import no.nav.security.token.support.core.api.ProtectedWithClaims
import no.nav.security.token.support.core.api.Unprotected
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
@ProtectedWithClaims(issuer = AzureAdConfig.ISSUER, claimMap = ["${AzureAdConfig.CLAIM_NAV_IDENT}=*"])
class RecipientsController(
	private val recipientsService: RecipientsService,
	private val securityContextHolder: SecurityContextHolder,
) : RecipientsApi {
	private final val logger: Logger = LoggerFactory.getLogger(javaClass)

	@Unprotected
	override fun getRecipients(): ResponseEntity<List<RecipientDto>> {
		return ResponseEntity.ok(recipientsService.getRecipients())
	}

	@Unprotected
	override fun getRecipient(recipientId: String): ResponseEntity<RecipientDto> {
		return ResponseEntity.ok(recipientsService.getRecipient(recipientId))
	}

	override fun createRecipient(newRecipientRequest: NewRecipientRequest): ResponseEntity<RecipientDto> {
		securityContextHolder.verifyWritePermission()
		val userId = securityContextHolder.getUserName()
		val dto = recipientsService.createRecipient(
			newRecipientRequest.recipientId,
			newRecipientRequest.name,
			newRecipientRequest.poBoxAddress,
			newRecipientRequest.postalCode,
			newRecipientRequest.postalName,
			userId,
		)
		return ResponseEntity.status(HttpStatus.CREATED).body(dto)
	}

	override fun updateRecipient(
		recipientId: String,
		updateRecipientRequest: UpdateRecipientRequest
	): ResponseEntity<RecipientDto> {
		securityContextHolder.verifyWritePermission()
		val userId = securityContextHolder.getUserName()
		val dto = recipientsService.updateRecipient(
			recipientId,
			updateRecipientRequest.name,
			updateRecipientRequest.poBoxAddress,
			updateRecipientRequest.postalCode,
			updateRecipientRequest.postalName,
			userId,
		)
		return ResponseEntity.ok(dto)
	}

	override fun deleteRecipient(recipientId: String): ResponseEntity<Unit> {
		securityContextHolder.verifyWritePermission()
		val userId = securityContextHolder.getUserName()
		recipientsService.deleteRecipient(recipientId)
		logger.info("User $userId deleted recipient $recipientId")
		return ResponseEntity.noContent().build()
	}

}
