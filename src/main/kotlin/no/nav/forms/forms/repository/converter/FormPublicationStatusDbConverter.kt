package no.nav.forms.forms.repository.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import no.nav.forms.forms.repository.entity.FormPublicationStatusDb

@Converter(autoApply = true)
class FormPublicationStatusDbConverter : AttributeConverter<FormPublicationStatusDb, String> {
	override fun convertToDatabaseColumn(attribute: FormPublicationStatusDb?): String? {
		return attribute?.value
	}

	override fun convertToEntityAttribute(dbData: String?): FormPublicationStatusDb? {
		return if (dbData != null) FormPublicationStatusDb.forValue(dbData) else null
	}
}
