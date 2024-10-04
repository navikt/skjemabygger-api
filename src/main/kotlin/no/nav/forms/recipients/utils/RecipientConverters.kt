package no.nav.forms.recipients.utils

import com.fasterxml.jackson.databind.JsonNode
import no.nav.forms.model.RecipientDto
import no.nav.forms.recipients.repository.RecipientEntity
import no.nav.forms.utils.mapDateTime
import java.util.Collections

private fun convertArchiveSubjectsToDto(jsonNode: JsonNode?): String? {
	if (jsonNode == null || !jsonNode.isArray) return null
	val result = mutableListOf<String>()
	for (node in jsonNode.iterator()) {
		result.add(node.asText())
	}
	return if (result.isEmpty()) null else Collections.unmodifiableList(result).joinToString(",")
}

fun RecipientEntity.toDto(): RecipientDto {
	return RecipientDto(
		recipientId = this.recipientId,
		name = this.name,
		poBoxAddress = this.poBoxAddress,
		postalCode = this.postalCode,
		postalName = this.postalName,
		createdAt = mapDateTime(this.createdAt),
		createdBy = this.createdBy,
		changedAt = mapDateTime(this.changedAt),
		changedBy = this.changedBy,
		archiveSubjects = convertArchiveSubjectsToDto(this.archiveSubjects),
	)
}
