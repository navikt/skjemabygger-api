package no.nav.forms.forms

import jakarta.transaction.Transactional
import no.nav.forms.exceptions.InvalidRevisionException
import no.nav.forms.exceptions.ResourceNotFoundException
import no.nav.forms.forms.repository.FormPublicationRepository
import no.nav.forms.forms.repository.FormRepository
import no.nav.forms.forms.repository.entity.FormPublicationEntity
import no.nav.forms.forms.utils.toFormDto
import no.nav.forms.model.FormDto
import no.nav.forms.model.PublishedTranslationsDto
import no.nav.forms.translations.form.repository.FormTranslationRepository
import no.nav.forms.translations.form.repository.PublishedFormTranslationRepository
import no.nav.forms.translations.form.repository.entity.PublishedFormTranslationsEntity
import no.nav.forms.translations.form.utils.mapToDictionary
import no.nav.forms.translations.global.repository.PublishedGlobalTranslationsRepository
import no.nav.forms.utils.LanguageCode
import no.nav.forms.utils.mapDateTime
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class FormPublicationsService(
	val formPublicationRepository: FormPublicationRepository,
	val formRepository: FormRepository,
	val publishedGlobalTranslationsRepository: PublishedGlobalTranslationsRepository,
	val publishedFormTranslationRepository: PublishedFormTranslationRepository,
	val formTranslationRepository: FormTranslationRepository,
) {

	@Transactional
	fun publishForm(formPath: String, formRevision: Int, userId: String): FormDto {
		val form = formRepository.findByPath(formPath) ?: throw IllegalArgumentException("Invalid form path: $formPath")

		val latestPublicationOfGlobalTranslations = publishedGlobalTranslationsRepository.findFirstByOrderByCreatedAtDesc()
			?: throw ResourceNotFoundException("Publication of global translations not found", "latest")

		val latestFormRevision = form.revisions.last()
		if (latestFormRevision.revision != formRevision) {
			throw InvalidRevisionException("Form revision is not the latest: $formRevision")
		}

		val latestFormTranslations = formTranslationRepository.findAllByFormPathAndDeletedAtIsNull(formPath)
			.map { it.revisions!!.last() }
			.filter { it.globalTranslation == null }

		val createdAt = LocalDateTime.now()
		val publishedFormTranslations = publishedFormTranslationRepository.save(
			PublishedFormTranslationsEntity(
				createdAt = createdAt,
				createdBy = userId,
				form = form,
				formTranslationRevisions = latestFormTranslations
			)
		)
		val formPublicationEntity = formPublicationRepository.save(
			FormPublicationEntity(
				createdAt = createdAt,
				createdBy = userId,
				formRevision = latestFormRevision,
				publishedFormTranslation = publishedFormTranslations,
				publishedGlobalTranslation = latestPublicationOfGlobalTranslations,
			)
		)
		return formPublicationEntity.toFormDto()
	}

	@Transactional
	fun getPublishedForm(formPath: String): FormDto {
		val form = formRepository.findByPath(formPath)
			?: throw ResourceNotFoundException("Form not found", formPath)
		val latestPublication = form.revisions.lastOrNull { it.publications.isNotEmpty() }?.publications?.last()
			?: throw ResourceNotFoundException("Form not published", formPath)
		return latestPublication.toFormDto()
	}

	@Transactional
	fun getPublishedForms(): List<FormDto> {
		return formRepository.findAll().filter { it.revisions.any { it.publications.isNotEmpty() } }
			.map { it.revisions.last { it.publications.isNotEmpty() } }
			.map { it.publications.last().toFormDto() }
	}

	@Transactional
	fun getPublishedFormTranslations(formPath: String, languageCodes: List<LanguageCode>?): PublishedTranslationsDto {
		val publication = formPublicationRepository.findFirstByFormRevisionFormPathOrderByCreatedAtDesc(formPath)
			?: throw ResourceNotFoundException("Form not published", formPath)
		val translations = languageCodes?.associate {
			it.value to publication.publishedFormTranslation.formTranslationRevisions.mapToDictionary(it)
		}
		return PublishedTranslationsDto(
			publishedAt = mapDateTime(publication.createdAt),
			publishedBy = publication.createdBy,
			translations = translations
		)
	}

}
