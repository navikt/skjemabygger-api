package no.nav.forms.recipients

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
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
				archiveSubjects = convertArchiveSubjectsToEntity(archiveSubjects),
			)
		)
		return entity.toDto()
	}

	fun convertArchiveSubjectsToEntity(source: String?): JsonNode? {
		if (source.isNullOrEmpty()) return null
		val subjects = source.split(",").map { it.trim() }
		return ObjectMapper().createArrayNode().apply { subjects.forEach { add(it) } }
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
				archiveSubjects = convertArchiveSubjectsToEntity(archiveSubjects),
				changedAt = now,
				changedBy = userId,
			)
		)
		return updatedEntity.toDto()
	}

	fun deleteRecipient(recipientId: String) {
		val entity = recipientsRepository.findByRecipientId(recipientId)
			?: throw ResourceNotFoundException("Recipient not found", recipientId)
		recipientsRepository.delete(entity)
	}
}
