package no.nav.forms.translations.form.repository

import no.nav.forms.translations.form.repository.entity.FormRevisionTranslationRevisionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormRevisionTranslationRevisionRepository: JpaRepository<FormRevisionTranslationRevisionEntity, Long> {

	fun findAllByFormRevisionFormPath(formPath: String): List<FormRevisionTranslationRevisionEntity>

	fun findByFormRevisionFormPathAndFormTranslationRevisionFormTranslationId(formPath: String, formTranslationId: Long): FormRevisionTranslationRevisionEntity?

	fun findByFormRevisionFormPathAndFormTranslationRevisionFormTranslationKey(formPath: String, key: String): FormRevisionTranslationRevisionEntity?

	fun findOneByFormRevisionFormPathAndFormTranslationRevisionId(formPath: String, formTranslationRevisionId: Long): FormRevisionTranslationRevisionEntity

}
