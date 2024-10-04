package no.nav.forms.builders

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.forms.recipients.repository.RecipientEntity
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class RecipientEntityTestdataBuilder(
	val id: Long? = Random.nextLong(),
	val recipientId: String = UUID.randomUUID().toString(),
	val name: String = "NAV Pensjon",
	val poBoxAddress: String = "Postboks 1",
	val postalCode: String = "0591",
	val postalName: String = "Oslo",
	val createdAt: LocalDateTime = LocalDateTime.now(),
	val createdBy: String = "testuser",
	val changedAt: LocalDateTime = LocalDateTime.now(),
	val changedBy: String = "testuser",
	val archiveSubjects: List<String>? = null,
) {

	private val mapper = ObjectMapper()

	fun build(): RecipientEntity = RecipientEntity(
		id = id,
		recipientId = this.recipientId,
		name = this.name,
		poBoxAddress = poBoxAddress,
		postalCode = postalCode,
		postalName =postalName,
		createdAt = createdAt,
		createdBy = createdBy,
		changedAt = changedAt,
		changedBy = changedBy,
		archiveSubjects = if (archiveSubjects != null) mapper.createArrayNode().apply {
			archiveSubjects.forEach { this.add(it) }
		} else null,
	)
}
