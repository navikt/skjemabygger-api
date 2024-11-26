package no.nav.forms.exceptions

class DuplicateResourceException(
	override val message: String,
	val resourceId: String,
) : RuntimeException(message)
