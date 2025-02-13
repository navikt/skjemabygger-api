package no.nav.forms.forms.repository

import no.nav.forms.forms.repository.entity.FormEntity
import no.nav.forms.forms.repository.entity.attributes.FormLockDb
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface FormRepository: JpaRepository<FormEntity, Long> {

	@EntityGraph(attributePaths = ["revisions", "publications", "publications.publishedFormTranslation", "publications.publishedGlobalTranslation"])
	fun findByPath(path: String): FormEntity?

	@Modifying
	@Query("update FormEntity f set f.lock = ?1 where f.id = ?2")
	fun setLockOnForm(lockDb: FormLockDb?, id: Long)

	fun existsBySkjemanummer(skjemanummer: String): Boolean

}
