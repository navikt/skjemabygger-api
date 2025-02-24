package no.nav.forms.forms.repository.entity.attributes

import java.time.OffsetDateTime

class FormLockDb(
	val createdAt: OffsetDateTime,
	val createdBy: String,
	val reason: String,
)
