package no.nav.forms.recipients

import no.nav.forms.model.RecipientDto
import no.nav.forms.recipients.repository.RecipientEntity
import no.nav.forms.recipients.repository.RecipientRepository
import no.nav.forms.recipients.utils.convertRecipientToDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class RecipientsService(
	private val recipientsRepository: RecipientRepository,
) {
	fun getRecipients(): List<RecipientDto> {
		val allRecipients = recipientsRepository.findAll()

		return allRecipients.map(::convertRecipientToDto)
	}

	fun createRecipient(
		recipientId: String?,
		name: String,
		poBoxAddress: String,
		postalCode: String,
		postalName: String,
		archiveSubjects: String?,
		userId: String,
	): RecipientDto {
		val now = LocalDateTime.now()
		val db = RecipientEntity(
			id = null,
			recipientId = recipientId ?: UUID.randomUUID().toString(),
			name = name,
			poBoxAddress = poBoxAddress,
			postalCode = postalCode,
			postalName = postalName,
			createdAt = now,
			createdBy = userId,
			changedAt = now,
			changedBy = userId,
			archiveSubjects = archiveSubjects,
		)
		val entity = recipientsRepository.save(db)
		return convertRecipientToDto(entity)
	}
}
