package no.nav.forms.translations.global.repository

import no.nav.forms.translations.global.repository.entity.GlobalTranslationRevisionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface GlobalTranslationRevisionRepository : JpaRepository<GlobalTranslationRevisionEntity, Long>
