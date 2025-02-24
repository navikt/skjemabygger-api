package no.nav.forms.translations.global.repository

import no.nav.forms.translations.global.repository.entity.PublishedGlobalTranslationsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PublishedGlobalTranslationsRepository : JpaRepository<PublishedGlobalTranslationsEntity, Long> {

	fun findFirstByOrderByCreatedAtDesc(): PublishedGlobalTranslationsEntity?

}
