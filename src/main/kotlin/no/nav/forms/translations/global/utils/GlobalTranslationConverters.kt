package no.nav.forms.translations.global.utils

import no.nav.forms.model.GlobalTranslationDto
import no.nav.forms.translations.global.repository.entity.GlobalTranslationEntity
import no.nav.forms.translations.global.repository.entity.GlobalTranslationRevisionEntity
import no.nav.forms.translations.global.repository.entity.PublishedGlobalTranslationsEntity
import no.nav.forms.utils.LanguageCode
import no.nav.forms.utils.mapDateTime
import java.time.LocalDateTime

fun GlobalTranslationEntity.toDto(publishedAt: LocalDateTime?, publishedBy: String?): GlobalTranslationDto {
	val latestRevision = this.getLatestRevision()!!
	return GlobalTranslationDto(
		id = this.id!!,
		key = this.key,
		tag = this.tag,
		revision = latestRevision.revision,
		nb = latestRevision.nb,
		nn = latestRevision.nn,
		en = latestRevision.en,
		changedAt = latestRevision.run { mapDateTime(this.createdAt) },
		changedBy = latestRevision.createdBy,
		publishedAt = if (publishedAt != null) mapDateTime(publishedAt) else null,
		publishedBy = publishedBy,
	)
}

fun GlobalTranslationEntity.isDeleted() = this.deletedAt != null

fun GlobalTranslationEntity.getLatestRevision(): GlobalTranslationRevisionEntity? = this.revisions?.lastOrNull()

fun GlobalTranslationRevisionEntity.getTranslation(languageCode: LanguageCode): String? {
	return when (languageCode) {
		LanguageCode.NB -> nb
		LanguageCode.NN -> nn
		LanguageCode.EN -> en
	}
}

fun List<GlobalTranslationRevisionEntity>.mapToDictionary(languageCode: LanguageCode): Map<String, String> {
	return associate { it.globalTranslation.key to it.getTranslation(languageCode) }
		.filter { it.value != null } as Map<String, String>
}

fun PublishedGlobalTranslationsEntity.containsGlobalTranslation(globalTranslation: GlobalTranslationEntity): Boolean {
	return this.globalTranslations.any { rev -> rev.globalTranslation.id == globalTranslation.id } == true
}
