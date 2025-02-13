package no.nav.forms.exceptions

class LockedResourceException(
	override val message: String,
) : RuntimeException(message)
