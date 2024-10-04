package no.nav.forms.recipients.utils

import com.fasterxml.jackson.databind.JsonNode
import no.nav.forms.model.RecipientDto
import no.nav.forms.recipients.repository.RecipientEntity
import no.nav.forms.utils.mapDateTime
import java.util.Collections

fun convertRecipientToDto(source: RecipientEntity): RecipientDto {
	return RecipientDto(
		recipientId = source.recipientId,
		name = source.name,
		poBoxAddress = source.poBoxAddress,
		postalCode = source.postalCode,
		postalName = source.postalName,
		createdAt = mapDateTime(source.createdAt),
		createdBy = source.createdBy,
		changedAt = mapDateTime(source.changedAt),
		changedBy = source.changedBy,
		archiveSubjects = convertArchiveSubjectsToDto(source.archiveSubjects),
	)
}

private fun convertArchiveSubjectsToDto(jsonNode: JsonNode?): String? {
	if (jsonNode == null || !jsonNode.isArray) return null
	val result = mutableListOf<String>()
	for (node in jsonNode.iterator()) {
		result.add(node.asText())
	}
	return if (result.isEmpty()) null else Collections.unmodifiableList(result).joinToString(",")
}
