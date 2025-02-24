package no.nav.forms.recipients

import no.nav.forms.exceptions.ResourceNotFoundException
import no.nav.forms.model.RecipientDto
import no.nav.forms.recipients.repository.RecipientEntity
import no.nav.forms.recipients.repository.RecipientRepository
import no.nav.forms.recipients.utils.toDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class RecipientsService(
	private val recipientsRepository: RecipientRepository,
) {
	fun getRecipients(): List<RecipientDto> {
		val allRecipients = recipientsRepository.findAll()
		return allRecipients.map { it.toDto() }
	}

	fun getRecipient(recipientId: String): RecipientDto {
		val entity = recipientsRepository.findByRecipientId(recipientId) ?:
		  throw ResourceNotFoundException("Recipient not found", recipientId)
		return entity.toDto()
	}

	fun createRecipient(
		recipientId: String?,
		name: String,
		poBoxAddress: String,
		postalCode: String,
		postalName: String,
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
			)
		)
		return entity.toDto()
	}

	fun updateRecipient(
		recipientId: String,
		name: String,
		poBoxAddress: String,
		postalCode: String,
		postalName: String,
		userId: String,
	): RecipientDto {
		val entity = recipientsRepository.findByRecipientId(recipientId)
			?: throw ResourceNotFoundException("Recipient not found", recipientId)

		entity.name = name
		entity.poBoxAddress = poBoxAddress
		entity.postalCode = postalCode
		entity.postalName = postalName
		entity.changedAt = LocalDateTime.now()
		entity.changedBy = userId
		val updatedEntity = recipientsRepository.save(entity)
		return updatedEntity.toDto()
	}

	fun deleteRecipient(recipientId: String) {
		val entity = recipientsRepository.findByRecipientId(recipientId)
			?: throw ResourceNotFoundException("Recipient not found", recipientId)
		recipientsRepository.delete(entity)
	}
}
