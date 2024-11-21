package no.nav.forms.translations.global

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import no.nav.forms.exceptions.ResourceNotFoundException
import no.nav.forms.model.GlobalTranslation
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
	val entityManager: EntityManager,
) {

	@Transactional
	fun getLatestRevisions(): List<GlobalTranslation> {
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
	): GlobalTranslation {
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
	): GlobalTranslation {
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

	fun deleteGlobalTranslation(id: Long, userId: String) {
		val globalTranslation = globalTranslationRepository.findById(id)
			.getOrElse { throw ResourceNotFoundException("Global translation not found", id.toString()) }

		globalTranslationRepository.save(
			globalTranslation.copy(
				deletedAt = LocalDateTime.now(),
				deletedBy = userId,
			)
		)
	}

}
