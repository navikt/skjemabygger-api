package no.nav.forms.forms.repository.entity

enum class FormPublicationStatusDb(val value: String) {

	Published("published"),
	Unpublished("unpublished");

	companion object {
		fun forValue(value: String): FormPublicationStatusDb {
			return FormPublicationStatusDb.entries.first {it.value == value}
		}
	}

}
