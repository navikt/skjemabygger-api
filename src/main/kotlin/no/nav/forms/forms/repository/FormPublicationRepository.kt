package no.nav.forms.forms.repository

import no.nav.forms.forms.repository.entity.FormPublicationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FormPublicationRepository: JpaRepository<FormPublicationEntity, Long> {

	fun findFirstByFormRevisionFormPathOrderByCreatedAtDesc(formPath: String): FormPublicationEntity?

}
