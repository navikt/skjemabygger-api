package no.nav.forms.recipients

import no.nav.forms.exceptions.ResourceNotFoundException
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

	fun getRecipient(recipientId: String): RecipientDto {
		val entity = recipientsRepository.findByRecipientId(recipientId) ?:
		  throw ResourceNotFoundException("Recipient not found", recipientId)
		return convertRecipientToDto(entity)
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
		val entity = recipientsRepository.save(
			RecipientEntity(
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
		)
		return convertRecipientToDto(entity)
	}

	fun updateRecipient(
		recipientId: String,
		name: String,
		poBoxAddress: String,
		postalCode: String,
		postalName: String,
		archiveSubjects: String?,
		userId: String,
	): RecipientDto {
		val entity = recipientsRepository.findByRecipientId(recipientId)
			?: throw ResourceNotFoundException("Recipient not found", recipientId)

		val now = LocalDateTime.now()
		val updatedEntity = recipientsRepository.save(
			entity.copy(
				name = name,
				poBoxAddress = poBoxAddress,
				postalCode = postalCode,
				postalName = postalName,
				archiveSubjects = archiveSubjects,
				changedAt = now,
				changedBy = userId,
			)
		)
		return convertRecipientToDto(updatedEntity)
	}
}
