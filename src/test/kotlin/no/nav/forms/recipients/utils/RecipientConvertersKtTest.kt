package no.nav.forms.recipients.utils

import no.nav.forms.builders.RecipientEntityTestdataBuilder
import no.nav.forms.recipients.repository.RecipientEntity
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class RecipientConvertersKtTest {

	@Test
	fun testDtoMappingOfCreatedAt() {
		val now = LocalDateTime.now()
		val entity: RecipientEntity = RecipientEntityTestdataBuilder(
			createdAt = now
		).build()
		val dto = entity.toDto()
		assertNotNull(dto.createdAt)
	}

}
