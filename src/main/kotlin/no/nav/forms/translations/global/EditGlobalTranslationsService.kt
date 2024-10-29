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

@Service
class EditGlobalTranslationsService(
	val globalTranslationRevisionRepository: GlobalTranslationRevisionRepository,
	val globalTranslationRepository: GlobalTranslationRepository,
	val entityManager: EntityManager,
) {

	@Transactional
	fun getLatestRevisions(): List<GlobalTranslation> {
		val all = globalTranslationRepository.findAll()
		return all.map(GlobalTranslationEntity::toDto)
	}

	@Transactional
	fun createGlobalTranslation(key: String, tag: String, nb: String?, nn: String?, en: String?): GlobalTranslation {
		val globalTranslation = globalTranslationRepository.save(
			GlobalTranslationEntity(
				key = key,
				tag = tag,
			)
		)
		globalTranslationRevisionRepository.save(
			GlobalTranslationRevisionEntity(
				nb = nb,
				nn = nn,
				en = en,
				createdAt = LocalDateTime.now(),
				createdBy = "test",
				globalTranslation = globalTranslation,
				revision = 1,
			)
		)
		entityManager.flush()
		entityManager.refresh(globalTranslation)
		return globalTranslation.toDto()
	}

	@Transactional
	fun updateGlobalTranslation(key: String, revision: Long, nb: String?, nn: String?, en: String?, userId: String): GlobalTranslation {
		val globalTranslation = globalTranslationRepository.findByKey(key)
			?: throw ResourceNotFoundException("Global translation not found", key)

		val latestRevision = globalTranslation.revisions?.lastOrNull()
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

}
