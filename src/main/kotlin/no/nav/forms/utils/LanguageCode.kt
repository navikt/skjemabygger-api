package no.nav.forms.utils

import com.fasterxml.jackson.annotation.JsonCreator

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
