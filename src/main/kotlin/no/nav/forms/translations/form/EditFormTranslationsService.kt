package no.nav.forms.translations.form

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import no.nav.forms.exceptions.ResourceNotFoundException
import no.nav.forms.model.FormTranslationDto
import no.nav.forms.translations.form.repository.FormTranslationRepository
import no.nav.forms.translations.form.repository.FormTranslationRevisionRepository
import no.nav.forms.translations.form.repository.entity.FormTranslationEntity
import no.nav.forms.translations.form.repository.entity.FormTranslationRevisionEntity
import no.nav.forms.translations.form.utils.toDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrElse

@Service
class EditFormTranslationsService(
	val formTranslationRepository: FormTranslationRepository,
	val formTranslationRevisionRepository: FormTranslationRevisionRepository,
	val entityManager: EntityManager,
) {

	@Transactional
	fun getTranslations(formPath: String): List<FormTranslationDto> {
		val formRevisionTranslations = formTranslationRepository.findAllByFormPath(formPath)
		return formRevisionTranslations.map(FormTranslationEntity::toDto)
	}

	@Transactional
	fun updateTranslation(
		formPath: String,
		id: Long,
		revision: Int,
		globalTranslationId: Long?,
		nb: String?,
		nn: String?,
		en: String?,
		userId: String,
	): FormTranslationDto {
		val formTranslation = formTranslationRepository.findById(id)
			.getOrElse { throw ResourceNotFoundException("Form translation not found", id.toString()) }
		if (formTranslation.formPath != formPath) {
			throw IllegalArgumentException("Illegal combination of form path and form translation id")
		}
		if (formTranslation.revisions?.any { it.revision == revision } == false) {
			throw IllegalArgumentException("Invalid revision: $revision")
		}
		formTranslationRevisionRepository.save(
			FormTranslationRevisionEntity(
				formTranslation = formTranslation,
				revision = revision + 1,
				nb = nb,
				nn = nn,
				en = en,
				createdAt = LocalDateTime.now(),
				createdBy = userId,
			)
		)
		entityManager.refresh(formTranslation)
		return formTranslation.toDto()
	}

	@Transactional
	fun createTranslation(
		formPath: String,
		key: String,
		globalTranslationId: Long?,
		nb: String?,
		nn: String?,
		en: String?,
		userId: String
	): FormTranslationDto {
		val formTranslation = formTranslationRepository.save(
			FormTranslationEntity(
				formPath = formPath,
				key = key,
			)
		)
		formTranslationRevisionRepository.save(
			FormTranslationRevisionEntity(
				revision = 1,
				formTranslation = formTranslation,
				nb = nb,
				nn = nn,
				en = en,
				createdAt = LocalDateTime.now(),
				createdBy = userId,
			)
		)
		entityManager.flush()
		entityManager.refresh(formTranslation)
		return formTranslation.toDto()
	}

}
