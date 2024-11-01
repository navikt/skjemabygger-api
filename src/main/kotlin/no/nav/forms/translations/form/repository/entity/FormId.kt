package no.nav.forms.translations.form.repository.entity

class FormId(
	val formPath: String?,
	val formTranslationRevisionId: Long?,
) {
	constructor() : this(null, null) {}
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false
		other as FormId

		return formPath == other.formPath && formTranslationRevisionId == other.formTranslationRevisionId
	}

	override fun hashCode(): Int = javaClass.hashCode()
}
