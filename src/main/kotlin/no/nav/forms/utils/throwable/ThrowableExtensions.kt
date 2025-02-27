package no.nav.forms.utils.throwable

fun Throwable.hasCause(targetExceptionClass: Class<out Throwable>): Boolean {
	var currentCause: Throwable? = this
	while (currentCause != null) {
		if (targetExceptionClass.isInstance(currentCause)) {
			return true
		}
		currentCause = this.cause
	}
	return false
}
