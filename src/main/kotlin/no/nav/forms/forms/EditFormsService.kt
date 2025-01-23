package no.nav.forms.forms

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import no.nav.forms.exceptions.InvalidRevisionException
import no.nav.forms.exceptions.ResourceNotFoundException
import no.nav.forms.forms.utils.toDto
import no.nav.forms.model.FormDto
import no.nav.forms.forms.repository.FormRepository
import no.nav.forms.forms.repository.FormRevisionRepository
import no.nav.forms.forms.repository.entity.FormEntity
import no.nav.forms.forms.repository.entity.FormRevisionEntity
import no.nav.forms.utils.Skjemanummer
import no.nav.forms.utils.toFormPath
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrElse

@Service
class EditFormsService(
	val formRepository: FormRepository,
	val formRevisionRepository: FormRevisionRepository,
) {
	val logger: Logger = LoggerFactory.getLogger(javaClass)
	private val mapper = ObjectMapper()

	@Transactional
	fun createForm(
		skjemanummer: Skjemanummer,
		title: String,
		components: List<Map<String, Any>>,
		properties: Map<String, Any>,
		userId: String,
	): FormDto {
		logger.info("New form created: $skjemanummer")
		val now = LocalDateTime.now()
		val form = formRepository.save(
			FormEntity(
				skjemanummer = skjemanummer,
				path = skjemanummer.toFormPath(),
				createdAt = now,
				createdBy = userId,
			)
		)

		val formRevision = formRevisionRepository.save(
			FormRevisionEntity(
				form = form,
				revision = 1,
				title = title,
				components = mapper.valueToTree(components),
				properties = mapper.valueToTree(properties),
				createdAt = now,
				createdBy = userId,
			)
		)

		return formRevision.toDto()
	}

	@Transactional
	fun getForm(id: Long): FormDto {
		val form =
			formRepository.findById(id).getOrElse { throw ResourceNotFoundException("Form not found", id.toString()) }
		return form.revisions.last().toDto()
	}

	@Transactional
	fun updateForm(
		id: Long,
		revision: Int,
		title: String,
		components: List<Map<String, Any>>,
		properties: Map<String, Any>,
		userId: String
	): FormDto {
		val form =
			formRepository.findById(id).getOrElse { throw ResourceNotFoundException("Form not found", id.toString()) }
		val latestFormRevision = form.revisions.last()
		if (latestFormRevision.revision != revision) {
			throw InvalidRevisionException("Unexpected global translation revision: $revision")
		}
		val formRevision = formRevisionRepository.save(
			FormRevisionEntity(
				form = form,
				revision = latestFormRevision.revision + 1,
				title = title,
				components = mapper.valueToTree(components),
				properties = mapper.valueToTree(properties),
				createdAt = LocalDateTime.now(),
				createdBy = userId,
			)
		)
		return formRevision.toDto()
	}

	@Transactional
	fun getForms(): List<FormDto> {
		return formRepository.findAll().map(FormEntity::toDto)
	}
}
