package no.nav.forms.forms.repository.entity

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class DbJsonArrayConverter : AttributeConverter<JsonNode, String> {

	private val mapper = ObjectMapper()

	override fun convertToDatabaseColumn(node: JsonNode?): String? {
		if (node == null) return null
		return node.toString()
	}

	override fun convertToEntityAttribute(data: String?): JsonNode? {
		if (data == null) return null;
		return if (data.isEmpty()) mapper.createArrayNode() else mapper.readTree(data)
	}

}
