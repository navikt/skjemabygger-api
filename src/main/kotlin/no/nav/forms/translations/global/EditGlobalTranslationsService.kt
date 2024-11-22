package no.nav.forms.translations.global

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import no.nav.forms.exceptions.ResourceNotFoundException
import no.nav.forms.model.GlobalTranslationDto
import no.nav.forms.translations.form.repository.FormTranslationRepository
import no.nav.forms.translations.global.repository.GlobalTranslationRepository
import no.nav.forms.translations.global.repository.GlobalTranslationRevisionRepository
import no.nav.forms.translations.global.repository.entity.GlobalTranslationEntity
import no.nav.forms.translations.global.repository.entity.GlobalTranslationRevisionEntity
import no.nav.forms.translations.global.utils.toDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrElse

@Service
class EditGlobalTranslationsService(
	val globalTranslationRevisionRepository: GlobalTranslationRevisionRepository,
	val globalTranslationRepository: GlobalTranslationRepository,
	val formTranslationRepository: FormTranslationRepository,
	val entityManager: EntityManager,
) {

	@Transactional
	fun getLatestRevisions(): List<GlobalTranslationDto> {
		val all = globalTranslationRepository.findAllByDeletedAtIsNull()
		return all.map(GlobalTranslationEntity::toDto)
	}

	@Transactional
	fun createGlobalTranslation(
		key: String,
		tag: String,
		nb: String?,
		nn: String?,
		en: String?,
		userId: String
	): GlobalTranslationDto {
		val globalTranslation = globalTranslationRepository.findByKey(key) ?: globalTranslationRepository.save(
			GlobalTranslationEntity(
				key = key,
				tag = tag,
			)
		)
		if (globalTranslation.deletedAt != null) {
			globalTranslationRepository.save(
				globalTranslation.copy(
					deletedAt = null,
					deletedBy = null,
					tag = tag,
				)
			)
		}
		val latestRevision = globalTranslation.revisions?.lastOrNull()
		globalTranslationRevisionRepository.save(
			GlobalTranslationRevisionEntity(
				nb = nb,
				nn = nn,
				en = en,
				createdAt = LocalDateTime.now(),
				createdBy = userId,
				globalTranslation = globalTranslation,
				revision = (latestRevision?.revision?.plus(1)) ?: 1,
			)
		)
		entityManager.flush()
		entityManager.refresh(globalTranslation)
		return globalTranslation.toDto()
	}

	@Transactional
	fun updateGlobalTranslation(
		id: Long,
		revision: Int,
		nb: String?,
		nn: String?,
		en: String?,
		userId: String
	): GlobalTranslationDto {
		val globalTranslation = globalTranslationRepository.findByIdAndDeletedAtIsNull(id)
			?: throw ResourceNotFoundException("Global translation not found", id.toString())
		if (globalTranslation.revisions?.any { it.revision == revision } == false) {
			throw IllegalArgumentException("Invalid revision: $revision")
		}
		globalTranslationRevisionRepository.save(
			GlobalTranslationRevisionEntity(
				nb = nb,
				nn = nn,
				en = en,
				createdAt = LocalDateTime.now(),
				createdBy = userId,
				globalTranslation = globalTranslation,
				revision = revision + 1,
			)
		)
		entityManager.refresh(globalTranslation)
		return globalTranslation.toDto()
	}

	@Transactional
	fun deleteGlobalTranslation(id: Long, userId: String) {
		val globalTranslation = globalTranslationRepository.findById(id)
			.getOrElse { throw ResourceNotFoundException("Global translation not found", id.toString()) }

		val isCurrentlyReferenced = formTranslationRepository.findAllByRevisionsGlobalTranslationId(globalTranslation.id!!)
			.any { it.revisions?.lastOrNull()?.globalTranslation?.id == id }
		if (isCurrentlyReferenced) {
			throw IllegalArgumentException("Cannot delete global translation since it is referenced by one or more form translations")
		}

		globalTranslationRepository.save(
			globalTranslation.copy(
				deletedAt = LocalDateTime.now(),
				deletedBy = userId,
			)
		)
	}

}
