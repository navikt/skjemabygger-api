package no.nav.forms.translations.form.validator

import no.nav.forms.model.NewFormTranslationRequestDto
import no.nav.forms.model.UpdateFormTranslationRequest

fun NewFormTranslationRequestDto.validate() {
	if (this.globalTranslationId != null) {
		if (this.nb != null || this.nn != null || this.en != null) {
			throw IllegalArgumentException("Do not provide local translations when linked to global translation")
		}
	}
}

fun UpdateFormTranslationRequest.validate() {
	if (this.globalTranslationId != null) {
		if (this.nb != null || this.nn != null || this.en != null) {
			throw IllegalArgumentException("Do not provide local translations when linked to global translation")
		}
	}
}
