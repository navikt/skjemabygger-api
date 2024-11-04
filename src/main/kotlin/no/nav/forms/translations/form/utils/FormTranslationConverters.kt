package no.nav.forms.translations.form.utils

import no.nav.forms.model.FormTranslationDto
import no.nav.forms.translations.form.repository.entity.FormTranslationEntity
import no.nav.forms.translations.form.repository.entity.FormTranslationRevisionEntity
import no.nav.forms.utils.mapDateTime

fun FormTranslationEntity.toDto(): FormTranslationDto {
	val latestRevision = this.revisions?.lastOrNull()
	return FormTranslationDto(
		id = this.id!!,
		key = this.key,
		revision = latestRevision?.revision,
		nb = latestRevision?.nb,
		nn = latestRevision?.nn,
		en = latestRevision?.en,
		changedAt = latestRevision?.run { mapDateTime(this.createdAt) },
		changedBy = latestRevision?.createdBy,
	)
}

fun FormTranslationRevisionEntity.toDto(): FormTranslationDto {
	return FormTranslationDto(
		id = this.formTranslation.id!!,
		key = this.formTranslation.key,
		revision = this.revision,
		nb = this.nb,
		nn = this.nn,
		en = this.en,
		changedAt = mapDateTime(this.createdAt),
		changedBy = this.createdBy,
	)
}
