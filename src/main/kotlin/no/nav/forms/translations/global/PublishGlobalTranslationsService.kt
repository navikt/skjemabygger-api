package no.nav.forms.translations.global

import jakarta.transaction.Transactional
import no.nav.forms.model.PublishedGlobalTranslationDto
import no.nav.forms.translations.global.repository.GlobalTranslationRepository
import no.nav.forms.translations.global.repository.PublishedGlobalTranslationsRepository
import no.nav.forms.translations.global.repository.entity.GlobalTranslationEntity
import no.nav.forms.translations.global.repository.entity.PublishedGlobalTranslationsEntity
import no.nav.forms.translations.global.utils.getLatestRevision
import no.nav.forms.translations.global.utils.getTranslation
import no.nav.forms.utils.LanguageCode
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PublishGlobalTranslationsService(
	private val publishedGlobalTranslationsRepository: PublishedGlobalTranslationsRepository,
	private val globalTranslationRepository: GlobalTranslationRepository,
) {

	fun getPublishedGlobalTranslations(languageCode: LanguageCode): List<PublishedGlobalTranslationDto> {
		val publishedGlobalTranslations = publishedGlobalTranslationsRepository.findFirstByOrderByCreatedAtDesc()
			?: throw Exception("No published global translations found")
		return publishedGlobalTranslations.globalTranslations
			.associate { it.globalTranslation.key to it.getTranslation(languageCode) }
			.filter { it.value != null }
			.map { PublishedGlobalTranslationDto(it.key, it.value!!) }
	}

	@Transactional
	fun publish(userId: String) {
		val globalTranslations = globalTranslationRepository.findAllByDeletedAtIsNull()
		val latestRevisions = globalTranslations.mapNotNull(GlobalTranslationEntity::getLatestRevision)
		publishedGlobalTranslationsRepository.save(
			PublishedGlobalTranslationsEntity(
				createdAt = LocalDateTime.now(),
				createdBy = userId,
				globalTranslations = latestRevisions
			)
		)
	}

}
