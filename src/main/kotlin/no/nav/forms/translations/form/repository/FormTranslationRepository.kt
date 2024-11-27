package no.nav.forms.translations.form.repository

import no.nav.forms.translations.form.repository.entity.FormTranslationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FormTranslationRepository: JpaRepository<FormTranslationEntity, Long> {

	fun findAllByRevisionsGlobalTranslationId(globalTranslationId: Long): List<FormTranslationEntity>

	fun findByFormPathAndKey(formPath: String, key: String): FormTranslationEntity?

}
