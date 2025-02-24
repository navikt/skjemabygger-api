package no.nav.forms.translations.form.repository

import no.nav.forms.translations.form.repository.entity.PublishedFormTranslationsEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PublishedFormTranslationRepository: JpaRepository<PublishedFormTranslationsEntity, Long>
