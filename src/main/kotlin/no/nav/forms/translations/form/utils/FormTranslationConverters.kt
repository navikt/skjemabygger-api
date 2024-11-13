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
	val latestGlobalRevision = if (this.globalTranslation?.id != null) this.globalTranslation.revisions?.lastOrNull() else null
	return FormTranslationDto(
		id = this.formTranslation.id!!,
		key = this.formTranslation.key,
		revision = this.revision,
		globalTranslationId = this.globalTranslation?.id,
		nb = if (latestGlobalRevision != null) latestGlobalRevision.nb else this.nb,
		nn = if (latestGlobalRevision != null) latestGlobalRevision.nn else this.nn,
		en = if (latestGlobalRevision != null) latestGlobalRevision.en else this.en,
		changedAt = mapDateTime(this.createdAt),
		changedBy = this.createdBy,
	)
}
