package no.nav.forms.translations.form

import jakarta.transaction.Transactional
import no.nav.forms.exceptions.ResourceNotFoundException
import no.nav.forms.model.FormTranslationDto
import no.nav.forms.translations.form.repository.FormRevisionTranslationRevisionRepository
import no.nav.forms.translations.form.repository.FormTranslationRepository
import no.nav.forms.translations.form.repository.FormTranslationRevisionRepository
import no.nav.forms.translations.form.repository.entity.FormRevisionTranslationRevisionEntity
import no.nav.forms.translations.form.repository.entity.FormTranslationEntity
import no.nav.forms.translations.form.repository.entity.FormTranslationRevisionEntity
import no.nav.forms.translations.form.utils.toDto
import no.nav.forms.translations.global.repository.GlobalTranslationRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrElse

@Service
class EditFormTranslationsService(
	val formTranslationRepository: FormTranslationRepository,
	val formTranslationRevisionRepository: FormTranslationRevisionRepository,
	val formRevisionTranslationRevisionRepository: FormRevisionTranslationRevisionRepository,
	val globalTranslationRepository: GlobalTranslationRepository,
) {

	@Transactional
	fun getTranslations(formPath: String): List<FormTranslationDto> {
		val currentFormTranslationRevisions = formRevisionTranslationRevisionRepository.findAllByFormPath(formPath)
		val revisionIds =
			currentFormTranslationRevisions.map(FormRevisionTranslationRevisionEntity::formTranslationRevisionId)
		val revisions = formTranslationRevisionRepository.findAllById(revisionIds)
		return revisions.map(FormTranslationRevisionEntity::toDto)
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
		val globalTranslation = if (globalTranslationId != null) {
			globalTranslationRepository.findById(globalTranslationId)
				.getOrElse { throw IllegalArgumentException("Global translation not found") }
		} else null

		val formTranslation = formTranslationRepository.findById(id)
			.getOrElse { throw ResourceNotFoundException("Form translation not found", id.toString()) }
		if (formTranslation.formPath != formPath) {
			throw IllegalArgumentException("Illegal combination of form path and form translation id")
		}
		if (formTranslation.revisions?.any { it.revision == revision } == false) {
			throw IllegalArgumentException("Invalid revision: $revision")
		}
		val latestRevision = formTranslation.revisions?.lastOrNull()
		val newFormTranslationRevision = formTranslationRevisionRepository.save(
			FormTranslationRevisionEntity(
				formTranslation = formTranslation,
				revision = revision + 1,
				globalTranslation = globalTranslation,
				nb = nb,
				nn = nn,
				en = en,
				createdAt = LocalDateTime.now(),
				createdBy = userId,
			)
		)
		val currentFormTranslationRevisionEntity =
			formRevisionTranslationRevisionRepository.findOneByFormPathAndFormTranslationRevisionId(
				formPath,
				latestRevision?.id!!
			)
		formRevisionTranslationRevisionRepository.save(
			currentFormTranslationRevisionEntity.copy(
				formTranslationRevisionId = newFormTranslationRevision.id!!
			)
		)
		return newFormTranslationRevision.toDto()
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
		val globalTranslation = if (globalTranslationId != null) {
			globalTranslationRepository.findById(globalTranslationId)
				.getOrElse { throw IllegalArgumentException("Global translation not found") }
		} else null

		val formTranslation = formTranslationRepository.save(
			FormTranslationEntity(
				formPath = formPath,
				key = key,
			)
		)
		val formTranslationRevision = formTranslationRevisionRepository.save(
			FormTranslationRevisionEntity(
				revision = 1,
				formTranslation = formTranslation,
				globalTranslation = globalTranslation,
				nb = nb,
				nn = nn,
				en = en,
				createdAt = LocalDateTime.now(),
				createdBy = userId,
			)
		)
		formRevisionTranslationRevisionRepository.save(
			FormRevisionTranslationRevisionEntity(
				formPath = formPath,
				formTranslationRevisionId = formTranslationRevision.id!!,
			)
		)
		return formTranslationRevision.toDto()
	}

}
