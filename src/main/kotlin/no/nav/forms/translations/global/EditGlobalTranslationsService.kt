package no.nav.forms.translations.global

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
	val globalTranslationRepository: GlobalTranslationRepository
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
		val revisionEntity = globalTranslationRevisionRepository.save(
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
		return globalTranslation.copy(
			revisions = listOf(revisionEntity)
		).toDto()
	}

	@Transactional
	fun updateGlobalTranslation(key: String, revision: Long, nb: String?, nn: String?, en: String?, userId: String): GlobalTranslation {
		val globalTranslation = globalTranslationRepository.findByKey(key)
			?: throw ResourceNotFoundException("Global translation not found", key)

		val latestRevision = globalTranslation.revisions?.lastOrNull()
		val revisionEntity = globalTranslationRevisionRepository.save(
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
		val updatedRevisions = globalTranslation.revisions?.plus(revisionEntity) ?: listOf(revisionEntity)
		return globalTranslation.copy(
			revisions = updatedRevisions
		).toDto()
	}

}
