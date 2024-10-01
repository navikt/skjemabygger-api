package no.nav.forms.exceptions

class ResourceNotFoundException(
	override val message: String,
	val resourceId: String,
	override val cause: Throwable? = null,
) : RuntimeException(message)
