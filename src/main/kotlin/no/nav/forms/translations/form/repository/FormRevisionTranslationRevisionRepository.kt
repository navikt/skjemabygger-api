package no.nav.forms.translations.form.repository

import no.nav.forms.translations.form.repository.entity.FormRevisionTranslationRevisionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormRevisionTranslationRevisionRepository: JpaRepository<FormRevisionTranslationRevisionEntity, Long> {

	fun findAllByFormRevisionId(formRevisionId: Long): List<FormRevisionTranslationRevisionEntity>

	fun findByFormRevisionIdAndFormTranslationRevisionFormTranslationId(formRevisionId: Long, formTranslationId: Long): FormRevisionTranslationRevisionEntity?

	fun findByFormRevisionIdAndFormTranslationRevisionFormTranslationKey(formRevisionId: Long, key: String): FormRevisionTranslationRevisionEntity?

	fun findOneByFormRevisionIdAndFormTranslationRevisionId(formRevisionId: Long, formTranslationRevisionId: Long): FormRevisionTranslationRevisionEntity

}
