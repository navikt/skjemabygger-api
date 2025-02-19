package no.nav.forms.exceptions

class ResourceDependencyException (
	override val message: String,
) : ConflictException(message)
