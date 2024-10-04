package no.nav.forms.recipients.utils

import no.nav.forms.builders.RecipientEntityTestdataBuilder
import no.nav.forms.recipients.repository.RecipientEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RecipientConvertersKtTest {

	@Test
	fun testDtoMappingOfArchiveSubjectsWhenNull() {
		val entity: RecipientEntity = RecipientEntityTestdataBuilder(
			archiveSubjects = null
		).build()
		val dto = convertRecipientToDto(entity)
		assertNull(dto.archiveSubjects)
	}

	@Test
	fun testDtoMappingOfArchiveSubjectsContainingTwoElements() {
		val entity: RecipientEntity = RecipientEntityTestdataBuilder(
			archiveSubjects = listOf("PEN", "BIL")
		).build()
		val dto = convertRecipientToDto(entity)
		assertEquals("PEN,BIL", dto.archiveSubjects)
	}

	@Test
	fun testDtoMappingOfArchiveSubjectsEmptyList() {
		val entity: RecipientEntity = RecipientEntityTestdataBuilder(
			archiveSubjects = emptyList()
		).build()
		val dto = convertRecipientToDto(entity)
		assertNull(dto.archiveSubjects)
	}

}
