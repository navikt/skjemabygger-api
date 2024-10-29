package no.nav.forms.translations.global

import no.nav.forms.translations.global.repository.PublishedGlobalTranslationsRepository
import org.springframework.stereotype.Service

@Service
class PublishedGlobalTranslationsService(
	val repository: PublishedGlobalTranslationsRepository
) {

	fun getGlobalTranslation(): Map<String, String?> {
		val publishedGlobalTranslations = repository.findFirstByOrderByCreatedAtDesc()
			?: throw Exception("No global translations found")
		return publishedGlobalTranslations.globalTranslations.associate { it.globalTranslation.key to it.nb }
	}
}
