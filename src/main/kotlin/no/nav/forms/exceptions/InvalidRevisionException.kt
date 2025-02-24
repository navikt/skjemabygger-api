package no.nav.forms.exceptions

class InvalidRevisionException(
	override val message: String,
) : ConflictException(message)
