package no.nav.forms.recipients.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecipientRepository : JpaRepository<RecipientEntity, Long> {

	fun findByRecipientId(recipientId: String): RecipientEntity?

}
