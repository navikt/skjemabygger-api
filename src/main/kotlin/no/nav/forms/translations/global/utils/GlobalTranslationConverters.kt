package no.nav.forms.translations.global.utils

import no.nav.forms.model.GlobalTranslation
import no.nav.forms.translations.global.repository.entity.GlobalTranslationEntity
import no.nav.forms.translations.global.repository.entity.GlobalTranslationRevisionEntity
import no.nav.forms.utils.LanguageCode
import no.nav.forms.utils.mapDateTime

fun GlobalTranslationEntity.toDto(): GlobalTranslation {
	val latestRevision = this.getLatestRevision()
	return GlobalTranslation(
		id = this.id!!,
		key = this.key,
		tag = this.tag,
		revision = latestRevision?.revision,
		nb = latestRevision?.nb,
		nn = latestRevision?.nn,
		en = latestRevision?.en,
		changedAt = latestRevision?.run { mapDateTime(this.createdAt) },
		changedBy = latestRevision?.createdBy,
	)
}

fun GlobalTranslationEntity.getLatestRevision(): GlobalTranslationRevisionEntity? = this.revisions?.lastOrNull()

fun GlobalTranslationRevisionEntity.getTranslation(languageCode: LanguageCode): String? {
	return when (languageCode) {
		LanguageCode.NB -> nb
		LanguageCode.NN -> nn
		LanguageCode.EN -> en
	}
}
