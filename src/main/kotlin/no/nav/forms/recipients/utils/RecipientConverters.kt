package no.nav.forms.recipients.utils

import no.nav.forms.model.RecipientDto
import no.nav.forms.recipients.repository.RecipientEntity
import no.nav.forms.utils.mapDateTime

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
		archiveSubjects = source.archiveSubjects,
	)
}
