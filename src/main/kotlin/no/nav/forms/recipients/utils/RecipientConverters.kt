package no.nav.forms.recipients.utils

import no.nav.forms.model.RecipientDto
import no.nav.forms.recipients.repository.RecipientEntity
import no.nav.forms.utils.mapDateTime

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
	)
}
