package no.nav.forms.translations.testdata

import no.nav.forms.model.NewGlobalTranslationRequest

class GlobalTranslationsTestdata {
	companion object {
		val translations: Map<String, NewGlobalTranslationRequest> = mapOf(
			"Fornavn" to NewGlobalTranslationRequest(
				key = "Fornavn",
				tag = "skjematekster",
				nb = null,
				nn = "Fornamn",
				en = "Given name",
			),
			"required" to NewGlobalTranslationRequest(
				key = "required",
				tag = "validering",
				nb = "Du må fylle ut: {{field}}",
				nn = "Du må fylle ut: {{field}}",
				en = "You must fill in: {{field}}",
			),
		)
	}
}
