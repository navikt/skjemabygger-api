package no.nav.forms.translations.form.repository

import no.nav.forms.translations.form.repository.entity.FormRevisionTranslationRevisionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormRevisionTranslationRevisionRepository: JpaRepository<FormRevisionTranslationRevisionEntity, Long> {

	fun findAllByFormPath(formPath: String): List<FormRevisionTranslationRevisionEntity>

	fun findByFormPathAndFormTranslationRevisionFormTranslationId(formPath: String, formTranslationId: Long): FormRevisionTranslationRevisionEntity?

	fun findByFormPathAndFormTranslationRevisionFormTranslationKey(formPath: String, key: String): FormRevisionTranslationRevisionEntity?

	fun findOneByFormPathAndFormTranslationRevisionId(formPath: String, formTranslationRevisionId: Long): FormRevisionTranslationRevisionEntity

}
