package no.nav.forms.translations.global.repository

import no.nav.forms.translations.global.repository.entity.GlobalTranslationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GlobalTranslationRepository : JpaRepository<GlobalTranslationEntity, Long> {

	fun findByKey(key: String): GlobalTranslationEntity?

}
