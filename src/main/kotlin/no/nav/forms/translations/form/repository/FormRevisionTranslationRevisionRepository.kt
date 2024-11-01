package no.nav.forms.translations.form.repository

import no.nav.forms.translations.form.repository.entity.FormId
import no.nav.forms.translations.form.repository.entity.FormRevisionTranslationRevisionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormRevisionTranslationRevisionRepository: JpaRepository<FormRevisionTranslationRevisionEntity, FormId> {

	fun findAllByFormPath(formPath: String): List<FormRevisionTranslationRevisionEntity>

}
