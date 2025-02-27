package no.nav.forms.utils

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

enum class LanguageCode(val value: String) {
	NB("nb"),
	NN("nn"),
	EN("en");

	companion object {
		@JvmStatic
		@JsonCreator
		fun forValue(value: String): LanguageCode? = entries.firstOrNull { it.value == value.lowercase() }

		fun validate(lang: String): LanguageCode {
			return forValue(lang) ?: throw IllegalArgumentException("Language code '$lang' is not supported")
		}
	}
}

fun String.splitLanguageCodes(): List<LanguageCode> = split(",").map { LanguageCode.validate(it.trim()) }
	.also {
		if (it.hasDuplicates() == true) {
			throw IllegalArgumentException("Language codes must contain distinct values: $it")
		}
	}

fun JsonNode.toLanguageCodes(): List<LanguageCode> = map { LanguageCode.validate(it.asText()) }

fun List<LanguageCode>.toJsonNode(): JsonNode = ObjectMapper().valueToTree(map { it.value })

fun List<LanguageCode>.hasDuplicates(): Boolean = this.distinct().size != this.size
