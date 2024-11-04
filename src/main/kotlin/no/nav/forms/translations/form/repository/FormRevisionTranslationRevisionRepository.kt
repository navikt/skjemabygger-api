package no.nav.forms.translations.form.repository

import no.nav.forms.translations.form.repository.entity.FormRevisionTranslationRevisionId
import no.nav.forms.translations.form.repository.entity.FormRevisionTranslationRevisionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormRevisionTranslationRevisionRepository: JpaRepository<FormRevisionTranslationRevisionEntity, FormRevisionTranslationRevisionId> {

	fun findAllByFormPath(formPath: String): List<FormRevisionTranslationRevisionEntity>

	fun findOneByFormPathAndFormTranslationRevisionId(formPath: String, formTranslationRevisionId: Long): FormRevisionTranslationRevisionEntity

}
