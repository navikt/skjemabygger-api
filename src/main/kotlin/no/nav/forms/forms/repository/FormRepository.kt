package no.nav.forms.forms.repository

import no.nav.forms.forms.repository.entity.FormEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FormRepository: JpaRepository<FormEntity, Long> {

	fun findByPath(path: String): FormEntity?

}
